package com.lee.controller;

import com.lee.enums.ExceptionEnum;
import com.lee.exception.MyException;
import com.lee.validate.ImageCode;
import com.lee.validate.ImageCodeGenerator;
import com.lee.validate.ValidateCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class ValidateCodeController {

    @Autowired
    private ImageCodeGenerator imageCodeGenerator;

    // security的session管理工具
    @Autowired
    private SessionStrategy sessionStrategy;
    public final static String SESSION_KEY = "SESSION_KEY_IMAGE_CODE";

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest req, HttpServletResponse resp) {
        // 1 生成图片验证码
        ImageCode imageCode = imageCodeGenerator.generate(new ServletWebRequest(req));
        // 2 只需要把验证码基类存入security的session中，不需要存图片
        ValidateCode validateCode = new ValidateCode(imageCode.getCode(), imageCode.getExpireTime());
        sessionStrategy.setAttribute(new ServletWebRequest(req),SESSION_KEY,validateCode);
        // 3 写出图片到客户端
        try {
            ImageIO.write(imageCode.getImage(), "JPEG", resp.getOutputStream());
        }catch (Exception e){
            log.info("图片验证码写出到客户端失败");
            throw new MyException(ExceptionEnum.CREATE_IMAGE_CODE_FAIL);
        }
    }




}
