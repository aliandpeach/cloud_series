package com.yk.bitcoin.controller;

import com.yk.base.controller.BaseController;
import com.yk.bitcoin.manager.KeyPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("key")
public class KeyController extends BaseController {

    @RequestMapping(method = RequestMethod.POST, value = "/key/generate", produces = "application/json")
    @ResponseBody
    public KeyPair generateKeyPair(@RequestParam Map<String, String> params) {
        KeyPair keyPair = new KeyPair();
        return keyPair;
    }
}
