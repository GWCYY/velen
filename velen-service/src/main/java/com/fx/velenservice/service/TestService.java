package com.fx.velenservice.service;

import com.fx.velencore.util.IdUtils;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test() {
        return IdUtils.getId();
    }

}
