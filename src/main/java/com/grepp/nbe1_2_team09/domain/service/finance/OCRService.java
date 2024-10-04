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
import com.grepp.nbe1_2_team09.controller.finance.dto.ChatGPTReqDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.ChatGPTResDTO;
import com.grepp.nbe1_2_team09.controller.finance.dto.ReceiptDTO;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiURL;
    @Value("${openai.api.key}")
    private String openAiKey;

    public Map<Integer, String> extractTextFromImage(String image) throws Exception {
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
            e.printStackTrace();
            log.warn(">>>> {} : {} <<<<", e, new AccountBookException(ExceptionMessage.OCR_ERROR));
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

    public ReceiptDTO formatting(ReceiptDTO receipt) {
        String date = receipt.getExpenseDate();
        String amount = receipt.getAmount();
        String resultDate="";
        try {

            //숫자만 남기고 문자 다 제거
            amount = amount.replaceAll("[^\\d]", "");

            ChatGPTReqDTO reqDTO = new ChatGPTReqDTO(model, date);

            RestTemplate restTemplate=new RestTemplate();
            restTemplate.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + openAiKey);
                return execution.execute(request, body);
            });

            ChatGPTResDTO chatGPTResDTO = restTemplate.postForObject(apiURL, reqDTO, ChatGPTResDTO.class);
            resultDate = chatGPTResDTO.getChoices().get(0).getMessage().getContent();

            if(resultDate.length()>19){ //만약 LocalDateTime 이외의 다른 문자가 들어오면 없애기(숫자, :, - ,T 제외 없애기)
                resultDate= resultDate.replaceAll("[^0-9T:-]", "");
            }


        }catch (Exception e){
            e.printStackTrace();
            log.warn(">>>> {} : {} <<<<", e, new AccountBookException(ExceptionMessage.FORMAT_ERROR));
            throw new AccountBookException(ExceptionMessage.FORMAT_ERROR);
        }
        return new ReceiptDTO(resultDate, amount);
    }
}

