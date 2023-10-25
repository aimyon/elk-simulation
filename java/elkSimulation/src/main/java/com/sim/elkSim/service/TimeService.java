package com.sim.elkSim.service;

import com.sim.elkSim.core.utils.BaseResp;

public interface TimeService {
    BaseResp playTime(String code,String startTime, Integer speed);

    BaseResp pauseTime();

    BaseResp backTime();

    BaseResp forwardTime();

    BaseResp finishedTime(String code);

    BaseResp resetTime(String code);

    BaseResp speed(Integer speed);
}
