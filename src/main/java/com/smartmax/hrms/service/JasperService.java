package com.smartmax.hrms.service;

import com.smartmax.hrms.entity.Payroll;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
@Service
public class JasperService {
    @Autowired
    DataSource dataSource;
    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

    public byte[] generatePaySlip(LocalDate period) throws IOException, JRException, SQLException {
        Connection connection = dataSource.getConnection();
        File payslip = ResourceUtils.getFile("classpath:payslip.jrxml");
        File details = ResourceUtils.getFile("classpath:details.jrxml");
        File deductions = ResourceUtils.getFile("classpath:deductions.jrxml");
        File earnings = ResourceUtils.getFile("classpath:earnings.jrxml");
        JRSaver.saveObject(JasperCompileManager.compileReport(details.getAbsolutePath()),"details.jasper");
        JRSaver.saveObject(JasperCompileManager.compileReport(earnings.getAbsolutePath()),"earnings.jasper");
        JRSaver.saveObject(JasperCompileManager.compileReport(deductions.getAbsolutePath()),"deductions.jasper");
        JasperReport payslipReport = JasperCompileManager.compileReport(payslip.getAbsolutePath());
        JRSaver.saveObject(payslipReport,"payslip.jasper");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paramPayPeriod", java.sql.Date.valueOf(period));
        JasperPrint jasperPrint = JasperFillManager.fillReport(payslipReport, parameters, connection);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        connection.close();
        return bytes;
    }

    public byte[] generateSinglePayslip(int payslipId) throws IOException, JRException, SQLException{
        Connection connection = dataSource.getConnection();
        File payroll = ResourceUtils.getFile("classpath:single_payslip.jrxml");
        JasperReport payslipReport = JasperCompileManager.compileReport(payroll.getAbsolutePath());
        JRSaver.saveObject(payslipReport,"single_payslip.jasper");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paramPayslipId", payslipId);
        JasperPrint jasperPrint = JasperFillManager.fillReport(payslipReport, parameters, connection);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        connection.close();
        return bytes;
    }

    public byte[] generatePayroll(int payrollId) throws IOException, JRException, SQLException{
        Connection connection = dataSource.getConnection();
        File payroll = ResourceUtils.getFile("classpath:monthly_payroll.jrxml");
        JasperReport payrollReport = JasperCompileManager.compileReport(payroll.getAbsolutePath());
        JRSaver.saveObject(payrollReport,"monthly_payroll.jasper");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paramPayrollId", payrollId);
        JasperPrint jasperPrint = JasperFillManager.fillReport(payrollReport, parameters, connection);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        connection.close();
        return bytes;
    }

    public byte[] generateNhif(Payroll payroll) throws IOException, JRException, SQLException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Connection connection = dataSource.getConnection();
        File nhif = ResourceUtils.getFile("classpath:nhif_2.jrxml");
        JasperReport nhifReport = JasperCompileManager.compileReport(nhif.getAbsolutePath());
        JRSaver.saveObject(nhifReport,"nhif_2.jasper");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paramPayPeriod", payroll.getDate());
        JasperPrint jasperPrint = JasperFillManager.fillReport(nhifReport, parameters, connection);
        Exporter<net.sf.jasperreports.export.ExporterInput, net.sf.jasperreports.export.XlsxReportConfiguration, net.sf.jasperreports.export.XlsxExporterConfiguration, net.sf.jasperreports.export.OutputStreamExporterOutput> exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
        exporter.exportReport();
        connection.close();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] generateP10Csv(Payroll payroll) throws IOException, JRException, SQLException{
        return null;
    }
}
