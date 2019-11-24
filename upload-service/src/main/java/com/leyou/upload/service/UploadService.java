package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.ApplicationProperties;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(ApplicationProperties.class)
public class UploadService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private ApplicationProperties applicationProperties;
    public String saveImage(MultipartFile file) {
        try {
//            文件类型校验
            String contentType = file.getContentType();
            if(!applicationProperties.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.FILE_TYPE_NOT_SUPPORT);
            }
//            文件类容校验
            BufferedImage read = ImageIO.read(file.getInputStream());
            if(read==null){
                log.info("文件类型不匹配");
                throw new LyException(ExceptionEnum.FILE_TYPE_NOT_SUPPORT);

            }
            //保存文件
            String extion= StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extion, null);
            String fullPath = storePath.getFullPath();


            //        返回路劲
            return applicationProperties.getBaseUrl()+fullPath;
        } catch (IOException e) {
            log.info("文件上传失败"+e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

    }
}
