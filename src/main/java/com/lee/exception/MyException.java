package com.lee.exception;

import com.lee.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyException extends RuntimeException{

    private ExceptionEnum exceptionEnum;

}
