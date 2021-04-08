package com.yk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JonsTest
 */

public class FastJsonTest
{
    @Test
    public void test()
    {
        List<UserModel> list = new ArrayList<>();
        list.add(new UserModel("1", "u1"));
        list.add(new UserModel("2", "u2"));
        list.add(new UserModel("3", "u3"));
        String json = JSONObject.toJSON(list).toString();
        List lr = JSONArray.parseObject(json, new TypeReference<List<UserModel>>()
        {
        });
        System.out.println(lr);
        
        UserModel[] users = new UserModel[]{new UserModel("1", "u1")};
        String jsonArr = JSONObject.toJSON(users).toString();
        UserModel[] lrArr2 = JSONArray.parseObject(jsonArr, new TypeReference<UserModel[]>()
        {
        });
        System.out.println(lrArr2);
        
        List<String> listString = new ArrayList<>();
        listString.add("k1");
        listString.add("k2");
        listString.add("k3");
        
        
        String[] arrString = new String[3];
        arrString[0] = "k1";
        arrString[1] = "k2";
        arrString[2] = "k3";
        
        String jsonList = JSON.toJSONString(listString);
        String jsonArry = JSON.toJSONString(arrString);
        
        System.out.println(jsonList);
        System.out.println(jsonArry);
        
        List<String> xx = JSONArray.parseObject(jsonList, new TypeReference<List<String>>()
        {
        });
        String[] yy = JSONArray.parseObject(jsonList, new TypeReference<String[]>()
        {
        });
        System.out.println(xx);
        System.out.println(yy);
    }
    
    
    @Data
    @AllArgsConstructor
    private static class UserModel
    {
        private String id;
        private String name;
    }
}
