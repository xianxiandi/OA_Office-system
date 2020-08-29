package com.web.oa.myexception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyHandlExceptionResvler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        String aa="请检查是否全部填写完成.....";
        if(e instanceof MyException){
           aa= ((MyException) e).getMsg();
        }
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("Msg",aa);
       modelAndView.setViewName("error");
        return modelAndView;
    }
}
