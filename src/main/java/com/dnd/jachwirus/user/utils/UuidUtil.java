package com.dnd.jachwirus.user.utils;

import java.util.UUID;

public class UuidUtil {
    public String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
