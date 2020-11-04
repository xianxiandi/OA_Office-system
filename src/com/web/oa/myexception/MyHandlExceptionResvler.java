package com.web.oa.myexception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyHandlExceptionResvler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        String aa="出现未知错误!请检查是否全部填写完成或是否正确操作.....";
        if(e instanceof MyException){
           aa= ((MyException) e).getMsg();
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("Msg",aa);
       modelAndView.setViewName("error");
        return modelAndView;
    }
}
