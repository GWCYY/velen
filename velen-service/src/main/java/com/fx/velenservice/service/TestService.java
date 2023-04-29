package com.fx.velenservice.service;

import com.fx.velencore.util.IdUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestService implements BaseService {


    public String test() {
        return IdUtils.getId();
    }

}
