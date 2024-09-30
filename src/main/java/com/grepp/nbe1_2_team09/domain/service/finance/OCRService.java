package com.grepp.nbe1_2_team09.domain.service.finance;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.AccountBookException;
import com.grepp.nbe1_2_team09.controller.finance.dto.ReceiptDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OCRService {

    public Map<Integer, String> extractTextFromImageUrl(String image) throws Exception {
        //-----웹 url 파싱
//        URL url = new URL(imageUrl);
//        ByteString imgBytes;
//        try (InputStream in = url.openStream()) {
//            imgBytes = ByteString.readFrom(in);
//        }
        ///---

        //----로컬 파일 돌리기
//        ByteString imgBytes;
//        try (InputStream in = new FileInputStream("/Users/kimkahyun/Desktop/데브코스/project_2/NBE1_2_Team9/src/main/resources/test2.jpg")) {
//            imgBytes = ByteString.readFrom(in);
//        }
        //

        // Base64 문자열을 바이트 배열로 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(image);
        ByteString imgBytes = ByteString.copyFrom(decodedBytes);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        Map<Integer, String> results = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    if(res.getError().getCode()==3){
                        log.warn(">>>> {} : {} <<<<", res, ExceptionMessage.IMAGE_NOT_FOUND);
                        throw new AccountBookException(ExceptionMessage.IMAGE_NOT_FOUND);
                    }else {
                        log.warn(">>>> {} : {} <<<<", res, ExceptionMessage.OCR_ERROR);
                        throw new AccountBookException(ExceptionMessage.OCR_ERROR);
                    }
                }
                stringBuilder.append(res.getFullTextAnnotation().getText());
            }
        }catch (Exception e){
            throw new AccountBookException(ExceptionMessage.OCR_ERROR);
        }
        int idx=0;
        if(!stringBuilder.isEmpty()){
            for(String str : stringBuilder.toString().split("\n")){
                results.put(idx++,str);
            }
        }
        return results;
    }

    public ReceiptDTO ReceiptFormatting(ReceiptDTO receipt) {
        String date=receipt.getExpenseDate();
        String amount=receipt.getAmount();

        //앞 문자 제거
        date = date.replaceFirst("^.*?(\\d)", "$1");
        //중간에 (요일) 제거
        date = date.replaceAll("\\s*\\(.*?\\)\\s*", " ");
        //숫자만 남기고 문자 다 제거
        amount = amount.replaceAll("[^\\d]", "");

        // 다양한 날짜 포맷 지원을 위한 포매터 구성
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))

                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 d일 HH시 mm분 ss초"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 M월 dd일 HH시 mm분 ss초"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분 ss초"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"))

                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 d일 HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 M월 dd일 HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))

                .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy.M.dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy.M.d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))

                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))


                .toFormatter();

        //2020-04-22 (수) 20:12:11

        // 출력 포맷
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ReceiptDTO resultReceiptDTO=new ReceiptDTO();
        try {
            LocalDateTime resultDate = LocalDateTime.parse(date, formatter);
            resultDate.format(outputFormatter);
            resultReceiptDTO.setExpenseDate(String.valueOf(resultDate));
            resultReceiptDTO.setAmount(amount);
        } catch (Exception e) {
            log.warn(">>>> {} : {} <<<<", date, new AccountBookException(ExceptionMessage.FORMAT_ERROR));
            throw  new AccountBookException(ExceptionMessage.FORMAT_ERROR);
        }
        return resultReceiptDTO;
    }
}