package com.hujiang.notifycenter.utils.template.core.support;


import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;

import com.alibaba.fastjson.JSON;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yangkai
 * @date 2018/9/30
 * @email yangkai@hujiang.com
 * @description
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({TemplateInfoDto.class})
public class TemplateInfoTypeHandler extends BaseTypeHandler<TemplateInfoDto> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TemplateInfoDto parameter, JdbcType jdbcType)
        throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public TemplateInfoDto getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSON.parseObject(rs.getString(columnName), TemplateInfoDto.class);
    }

    @Override
    public TemplateInfoDto getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex), TemplateInfoDto.class);
    }

    @Override
    public TemplateInfoDto getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex), TemplateInfoDto.class);
    }
}
