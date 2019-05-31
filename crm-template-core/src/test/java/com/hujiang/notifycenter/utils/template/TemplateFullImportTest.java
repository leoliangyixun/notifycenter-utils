package com.hujiang.notifycenter.utils.template;

import com.hujiang.notifycenter.template.common.Helper;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-05-29
 * @email yangkai@hujiang.com
 * @description
 */
public class TemplateFullImportTest {

    @Test
    public void test_full_import() throws Exception {
        List<Integer> ids = Lists.newArrayList(5641, 5642, 5643, 5644, 5645, 5646, 5647, 5648, 5649, 5650, 5651);
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < ids.size() ; i++) {
            if (i == ids.size() - 1) {
                sb.append("'").append("CRM导入[" + Helper.encrypt(String.valueOf(ids.get(i))) + "]").append("'");
            } else {
                sb.append("'").append("CRM导入[" + Helper.encrypt(String.valueOf(ids.get(i))) + "]").append("'").append(", ");
            }
        }
        sb.append(")");
        System.out.println(sb.toString());
    }


    @Test
    public void test2() throws Exception {
        System.out.println(Helper.decrypt("8e90e286677686f6"));
    }

}
