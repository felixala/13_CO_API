package com.felixlaura.service;

import com.felixlaura.binding.PdfReport;
import com.felixlaura.entity.CitizenAppEntity;
import com.felixlaura.entity.CoTriggerEntity;
import com.felixlaura.entity.DcCaseEntity;
import com.felixlaura.entity.EligDtlsEntity;
import com.felixlaura.repository.CitizenAppRepository;
import com.felixlaura.repository.CoTriggerRepository;
import com.felixlaura.repository.DcCaseRepo;
import com.felixlaura.repository.EligDtlsRepository;
import com.felixlaura.utils.EmailUtils;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class CoTriggerServiceImpl implements CoTriggerService{

    @Autowired
    private CoTriggerRepository coTriggerRepository;

    @Autowired
    private EligDtlsRepository eligDtlsRepository;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private CitizenAppRepository citizenAppRepository;

    @Autowired
    private DcCaseRepo dcCaseRepo;
    Logger logger = LoggerFactory.getLogger(CoTriggerServiceImpl.class);


//    public List<PdfReport> generatePdfPendingTrigger(String trgStatus){
//
//        CoTriggerEntity queryBuilder = new CoTriggerEntity();
//        String status = "Pending";
//        queryBuilder.setTrgStatus(status);
//
//        Example<CoTriggerEntity> example = Example.of(queryBuilder);
//        List<CoTriggerEntity> entities = coTriggerRepository.findAll(example);
//
//        for(CoTriggerEntity entity : entities){
//            EligDtlsEntity eligDtlsEntity = eligDtlsRepository.findByCaseNum(entity.getCaseNum());
//            PdfReport pdfReport = new PdfReport();
//            BeanUtils.copyProperties(eligDtlsEntity, pdfReport);
//
//
//
//        }
//
//        return  null;
//    }

    @Override
    public void pdfReport(HttpServletResponse response) throws IOException {

        CoTriggerEntity queryBuilder = new CoTriggerEntity();
        String status = "Pending";
        queryBuilder.setTrgStatus(status);

        Example<CoTriggerEntity> example = Example.of(queryBuilder);
        List<CoTriggerEntity> entities = coTriggerRepository.findAll(example);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.blue);

        Paragraph p = new Paragraph("Correspondence Notice", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        //Number of columns
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{3.0f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        table.setSpacingBefore(10);

        //Write Table Header
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Name", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Name", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Status", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan Start Date", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Plan End Date", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Benefit Amount", fontHeader));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Denial Reason", fontHeader));
        table.addCell(cell);

        String subject = "Your Registration Success";
        String fileName = "TRG-EMAIL-BODY.txt";
        //Printing Valued in Pdf Report
        for (CoTriggerEntity entity : entities) {

            EligDtlsEntity eligDtlsEntity = eligDtlsRepository.findByCaseNum(entity.getCaseNum());

            PdfReport pdfReport = new PdfReport();
            BeanUtils.copyProperties(eligDtlsEntity, pdfReport);

            table.addCell(eligDtlsEntity.getHolderName());
            table.addCell(eligDtlsEntity.getPlanName());
            table.addCell(eligDtlsEntity.getPlanStatus());
            table.addCell(eligDtlsEntity.getPlanStartDate().toString());
            table.addCell(eligDtlsEntity.getPlanEndDate().toString());
            table.addCell(String.valueOf(eligDtlsEntity.getBenefitAmt()));
            table.addCell(eligDtlsEntity.getDenialReason());

//            table.addCell(eligDtlsEntity.getHolderName());
//            table.addCell(eligDtlsEntity.getPlanName());
//            table.addCell(eligDtlsEntity.getPlanStatus());
//            table.addCell(String.valueOf(eligDtlsEntity.getPlanStartDate()));
//            table.addCell(String.valueOf(eligDtlsEntity.getPlanEndDate()));
//            table.addCell(String.valueOf(eligDtlsEntity.getBenefitAmt()));
//            table.addCell(eligDtlsEntity.getDenialReason());

            document.add(table);

            document.close();

            String body = readRegEmailBody(pdfReport.getHolderName(), fileName);

            Optional<DcCaseEntity> dcCase = dcCaseRepo.findById(entity.getCaseNum());

            if(dcCase.isPresent()){
                DcCaseEntity dcCaseEntity = dcCase.get();
                Integer appId = dcCaseEntity.getAppId();

                Optional<CitizenAppEntity> citizenAppEntityById = citizenAppRepository.findById(appId);
                if(citizenAppEntityById.isPresent()){
                    CitizenAppEntity citizenAppEntity = citizenAppEntityById.get();
                    emailUtils.sendEmail(citizenAppEntity.getEmail(), subject, body);
                }

                entity.setTrgStatus("Completed");
            }

        }

    }

    private String readRegEmailBody(String fullName, String fileName) {
        String url = "";
        String mailBody = null;
        try (FileReader file = new FileReader(fileName); BufferedReader br = new BufferedReader(file)) {
            StringBuilder buffer = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                buffer.append(line);
                line = br.readLine(); // Read next line
            }
            mailBody = buffer.toString();
            mailBody = mailBody.replace("{FULL_NAME}", fullName);
            mailBody = mailBody.replace("{URL}", url);


        } catch (Exception e) {
            logger.error("Exception Occurred", e);
        }
        return mailBody;

    }

}