package com.smartmax.hrms.controllers;

import com.google.common.io.ByteSource;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.smartmax.hrms.entity.Payroll;
import com.smartmax.hrms.entity.Payslip;
import com.smartmax.hrms.repository.PayrollRepository;
import com.smartmax.hrms.repository.PayslipRepository;
import com.smartmax.hrms.service.ApachePoiService;
import com.smartmax.hrms.service.JasperService;
import com.smartmax.hrms.service.PayrollService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class JasperController {
    @Autowired
    JasperService jasperService;

    @Autowired
    PayslipRepository payslipRepository;

    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    ApachePoiService apachePoiService;

    @Autowired
    PayrollService payrollService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/api/hr/payroll/generate/{year}/{month}")
    public ResponseEntity<byte[]>printPayroll(@PathVariable("year")String year,@PathVariable("month")String month) throws JRException, SQLException, IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        if (payroll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        byte[] bytes = jasperService.generatePayroll(payroll.get().getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = payroll.get().getDate().getMonth() + String.valueOf(payroll.get().getDate().getYear()) + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/api/hr/payslips/generate/{employeeId}/{year}/{month}")
    public ResponseEntity<byte[]>printPayslipByEmployee(@PathVariable("employeeId")String employeeId, @PathVariable("year")String year, @PathVariable("month")String month) throws JRException, SQLException, IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payslip> payslip = payslipRepository.findByEmployeeIdAndpayPeriod(Integer.parseInt(employeeId),date);
        if(payslip.isEmpty()){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        byte[] bytes = jasperService.generateSinglePayslip(payslip.get().getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = payslip.get().getEmployee().getLastName()+ payslip.get().getPayPeriod().getMonth() + String.valueOf(payslip.get().getPayPeriod().getYear()) + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes,HttpStatus.OK);
    }

    @GetMapping("/api/hr/payroll/payslips/generate/{year}/{month}")
    public ResponseEntity<byte[]>printPayrollAndPayslips(@PathVariable("year")String year, @PathVariable("month")String month,HttpServletResponse response) throws JRException, SQLException, IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        List<byte []>byteList =  new ArrayList<>();
        if (payroll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        byte[] payrollByte = jasperService.generatePayroll(payroll.get().getId());
        byteList.add(payrollByte);
        byte[] payslipsBytes = jasperService.generatePaySlip(payroll.get().getDate());
        byteList.add(payslipsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = payroll.get().getDate().getMonth() + String.valueOf(payroll.get().getDate().getYear()) + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Document document = null;
        PdfCopy writer = null;
        for (byte[] pdfByteArray : byteList) {
            try {
                PdfReader reader = new PdfReader(pdfByteArray);
                int numberOfPages = reader.getNumberOfPages();

                if (document == null) {
                    document = new Document(reader.getPageSizeWithRotation(1));
                    writer = new PdfCopy(document, outStream); // new
                    document.open();
                }
                PdfImportedPage page;
                for (int i = 0; i < numberOfPages;) {
                    ++i;
                    page = writer.getImportedPage(reader, i);
                    writer.addPage(page);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
        document.close();
        outStream.close();
        return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/api/download/employee/sample")
    public ResponseEntity<byte[]>downloadsample() throws IOException {
        File file = ResourceUtils.getFile("classpath:sample.csv");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("sample.csv", "sample.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        return new ResponseEntity<>(Files.readAllBytes(file.toPath()), headers, HttpStatus.OK);
    }

    @GetMapping("/api/hr/payslips/generate/{year}/{month}")
    public ResponseEntity<byte[]>printAllPayslips(@PathVariable("year")String year, @PathVariable("month")String month,HttpServletResponse response) throws JRException, SQLException, IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        List<byte []>byteList =  new ArrayList<>();
        if (payroll.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        byte[] payslipsBytes = jasperService.generatePaySlip(date);
        byteList.add(payslipsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = payroll.get().getDate().getMonth() + String.valueOf(payroll.get().getDate().getYear()) + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Document document = null;
        PdfCopy writer = null;
        for (byte[] pdfByteArray : byteList) {
            try {
                PdfReader reader = new PdfReader(pdfByteArray);
                int numberOfPages = reader.getNumberOfPages();

                if (document == null) {
                    document = new Document(reader.getPageSizeWithRotation(1));
                    writer = new PdfCopy(document, outStream); // new
                    document.open();
                }
                PdfImportedPage page;
                for (int i = 0; i < numberOfPages;) {
                    ++i;
                    page = writer.getImportedPage(reader, i);
                    writer.addPage(page);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
        document.close();
        outStream.close();
        return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/api/hr/print/payroll/nhif/{year}/{month}")
    public void generateNhif(@PathVariable("year")String year, @PathVariable("month")String month,HttpServletResponse response) throws IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        byte[] bytes = apachePoiService.generateNhifByProduct(payroll.get());
        String fileName = year +"/"+month+"nhif.xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentLength(bytes.length);
        OutputStream os = response.getOutputStream();
        try {os.write(bytes , 0, bytes.length);} catch (Exception e) {
        } finally {os.close();}
    }

    @GetMapping("/api/hr/print/payroll/nssf/{year}/{month}")
    public void generateNssf(@PathVariable("year")String year, @PathVariable("month")String month,HttpServletResponse response) throws IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        byte[] bytes = apachePoiService.generateNssfByProduct(payroll.get());
        String fileName = year +"/"+month+"nssf.xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentLength(bytes.length);
        OutputStream os = response.getOutputStream();
        try {os.write(bytes , 0, bytes.length);} catch (Exception e) {
        } finally {os.close();}
    }

    @GetMapping(value = "/api/hr/print/p10/{year}/{month}", produces = "text/csv")
    public ResponseEntity<InputStreamResource> exportP10CSV(@PathVariable("year")String year, @PathVariable("month")String month) throws IOException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        Optional<Payroll> payroll = payrollRepository.findByDate(date);
        if(payroll.isEmpty()){
            return  new ResponseEntity<>(null,HttpStatus.CONFLICT);
        }
        ByteArrayOutputStream byteArrayOutputStream = payrollService.createP10Csv(payroll.get());
        InputStreamResource fileInputStream = new InputStreamResource(ByteSource.wrap(byteArrayOutputStream.toByteArray()).openStream());
        String csvFileName = "p10-"+year+"_"+month+".csv";

        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(
                fileInputStream,
                headers,
                HttpStatus.OK
        );
    }

}
