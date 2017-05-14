package com.groglogs.sample.exchange.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.groglogs.sample.exchange.pojo.Xrate;
import com.groglogs.sample.exchange.loader.XrateLoader;

@Controller
public class SampleController {

    @ResponseBody
    @RequestMapping("/api/getXrate/{currency}")
    public Xrate getXrate(@PathVariable String currency) throws Exception {
        if(XrateLoader.xrates == null){
            XrateLoader.doLoadXRates();
            return new Xrate("Data not available, retry later");
        }

        return new Xrate(currency, XrateLoader.xrates.get(currency));
    }

    @ResponseBody
    @RequestMapping("/api/getXrate/{currency}/{date}")
    public Xrate getXrate(@PathVariable String currency, @PathVariable String date) throws Exception {
        if(XrateLoader.hist_xrates == null){
            XrateLoader.doLoadXRates();
            return new Xrate("Data not available, retry later");
        }

        return new Xrate(currency, XrateLoader.hist_xrates.get(date).get(currency), date);
    }
}
