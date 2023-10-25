package com.sim.elkSim.websocket;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RestController

public class SystemController{
    @GetMapping("/socket/push/{cid}")
    public Map pushMessage(@PathVariable("cid") String cid, String message){
        Map<String, Object> result = new HashMap<>();
        try {
            HashSet<String> sids = new HashSet<>();
            sids.add(cid);
            WebSocketServer.sendMessage("service message:"+message, sids);
            result.put("code", cid);
            result.put("msg", message);
        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
