package com.smartmax.hrms.service;

import com.smartmax.hrms.entity.Employee;
import com.smartmax.hrms.entity.General;
import com.smartmax.hrms.entity.Payroll;
import com.smartmax.hrms.repository.GeneralRepository;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApachePoiService {
    @Autowired
    GeneralRepository generalRepository;

    public byte[] generateNssfByProduct(Payroll payroll) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        List<General> generals= generalRepository.findAll().stream().collect(Collectors.toList());
        General general = null;
        if(generals.size()>0){
             general =  generals.get(0);
        }
        else{
             general = new General("null","null");
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        Font font= workbook.createFont();
        font.setBold(true);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        XSSFSheet sheet = workbook.createSheet("nssf");
        XSSFRow row0 = sheet.createRow(0);
        XSSFCell payrollNo = row0.createCell(0);
        payrollNo.setCellStyle(cellStyle);
        payrollNo.setCellValue("PAYROLL NUMBER");
        XSSFCell lastName = row0.createCell(1);
        lastName.setCellStyle(cellStyle);
        lastName.setCellValue("SURNAME");
        XSSFCell firstName = row0.createCell(2);
        firstName.setCellStyle(cellStyle);
        firstName.setCellValue("OTHER NAMES");
        XSSFCell IdNo = row0.createCell(3);
        IdNo.setCellStyle(cellStyle);
        IdNo.setCellValue("ID NO");
        XSSFCell kraPin = row0.createCell(4);
        kraPin.setCellStyle(cellStyle);
        kraPin.setCellValue("KRA PIN");
        XSSFCell nssfPin = row0.createCell(5);
        nssfPin.setCellStyle(cellStyle);
        nssfPin.setCellValue("NSSF NO");
        XSSFCell grossPay = row0.createCell(6);
        grossPay.setCellStyle(cellStyle);
        grossPay.setCellValue("GROSS PAY");
        XSSFCell voluntary = row0.createCell(7);
        voluntary.setCellStyle(cellStyle);
        voluntary.setCellValue("VOLUNTARY");
        for(int i= 0;i<payroll.getPayslips().size();i++){
            Employee employee = payroll.getPayslips().get(i).getEmployee();
            XSSFRow row = sheet.createRow(i+1);
            XSSFCell payrollValue = row.createCell(0);
            payrollValue.setCellValue(employee.getPayrollNumber());
            XSSFCell lastNameValue = row.createCell(1);
            lastNameValue.setCellValue(employee.getLastName());
            XSSFCell firstNameValue = row.createCell(2);
            firstNameValue.setCellValue(employee.getFirstName());
            XSSFCell IdNoValue = row.createCell(3);
            IdNoValue.setCellValue(employee.getNationalId());
            XSSFCell kraPinValue = row.createCell(4);
            kraPinValue.setCellValue(employee.getKra());
            XSSFCell nssfPinValue = row.createCell(5);
            nssfPinValue.setCellValue(employee.getNhifPin());
            XSSFCell grossPayValue = row.createCell(6);
            grossPayValue.setCellValue(payroll.getPayslips().get(i).getGrossSalary() + payroll.getPayslips().get(i).getNssf());
            XSSFCell voluntaryValue = row.createCell(7);
            voluntaryValue.setCellValue(payroll.getPayslips().get(i).getNssf());
        }
        workbook.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        workbook.close();
        return bytes;
    }

    public byte[] generateNhifByProduct(Payroll payroll) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        List<General> generals= generalRepository.findAll().stream().collect(Collectors.toList());
        General general = null;
        if(generals.size()>0){
             general =  generals.get(0);
        }
        else{
             general = new General("null","null");
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFFont font= workbook.createFont();
        font.setBold(true);
        CellStyle cellStyle= workbook.createCellStyle();
        cellStyle.setFont(font);
        HSSFSheet sheet = workbook.createSheet("nhif");
        sheet.autoSizeColumn(1);
        HSSFRow row0 = sheet.createRow(0);
        HSSFCell employerCodeLabel = row0.createCell(0);
        employerCodeLabel.setCellStyle(cellStyle);
        employerCodeLabel.setCellValue("EMPLOYER CODE");
        HSSFCell employerCodeValue = row0.createCell(1);
        employerCodeValue.setCellValue(general.getEmployerCode());
        HSSFRow row1 = sheet.createRow(1);
        HSSFCell employerNameLabel = row1.createCell(0);
        employerNameLabel.setCellStyle(cellStyle);
        employerNameLabel.setCellValue("EMPLOYER NAME");
        HSSFCell employerNameValue = row1.createCell(1);
        employerNameValue.setCellValue(general.getEmployerName());
        HSSFRow row2 = sheet.createRow(2);
        HSSFCell monthLabel = row2.createCell(0);
        monthLabel.setCellStyle(cellStyle);
        monthLabel.setCellValue("MONTH OF CONTRIBUTION");
        HSSFCell monthValue = row2.createCell(1);
        HSSFDataFormat format = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("yyyy-mm"));
        monthValue.setCellStyle(dateStyle);
        monthValue.setCellValue(payroll.getDate());
        sheet.createRow(3);
        HSSFRow row4 = sheet.createRow(4);
        HSSFCell payrollNo = row4.createCell(0);
        payrollNo.setCellStyle(cellStyle);
        payrollNo.setCellValue("PAYROLL NO");
        HSSFCell lastName = row4.createCell(1);
        lastName.setCellStyle(cellStyle);
        lastName.setCellValue("LAST NAME");
        HSSFCell firstName = row4.createCell(2);
        firstName.setCellStyle(cellStyle);
        firstName.setCellValue("FIRST NAME");
        HSSFCell IdNo = row4.createCell(3);
        IdNo.setCellStyle(cellStyle);
        IdNo.setCellValue("ID NO");
        HSSFCell nhifNo = row4.createCell(4);
        nhifNo.setCellStyle(cellStyle);
        nhifNo.setCellValue("NHIF NO");
        HSSFCell amount = row4.createCell(5);
        amount.setCellStyle(cellStyle);
        amount.setCellValue("AMOUNT");
        int rowCount = 5;
        if(payroll.getPayslips().size()>0) {
            for (int i = 0; i < payroll.getPayslips().size(); i++) {
                Employee employee = payroll.getPayslips().get(i).getEmployee();
                HSSFRow row = sheet.createRow(rowCount);
                HSSFCell payrollValue = row.createCell(0);
                payrollValue.setCellValue(employee.getPayrollNumber());
                HSSFCell lastNameValue = row.createCell(1);
                lastNameValue.setCellValue(employee.getLastName());
                HSSFCell firstNameValue = row.createCell(2);
                String name = employee.getFirstName();
                firstNameValue.setCellValue(name);
                HSSFCell idNoValue = row.createCell(3);
                idNoValue.setCellValue(employee.getNationalId());
                HSSFCell nhifNoValue = row.createCell(4);
                nhifNoValue.setCellValue(employee.getNhifPin());
                HSSFCell amountValue = row.createCell(5);
                amountValue.setCellValue((double) payroll.getPayslips().get(i).getNhif());
                rowCount++;
            }
        }
        HSSFRow rowTotal = sheet.createRow(rowCount);
        HSSFCell totalLabel = rowTotal.createCell(4);
        totalLabel.setCellValue("TOTAL");
        totalLabel.setCellStyle(cellStyle);
        HSSFCell totalValue = rowTotal.createCell(5);
        totalValue.setCellValue(payroll.getNhif());
        workbook.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        workbook.close();
        return bytes;
    }
}
