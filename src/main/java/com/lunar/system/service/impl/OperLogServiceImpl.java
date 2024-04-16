package com.lunar.system.service.impl;

import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.system.entity.OperLog;
import com.lunar.system.mapper.OperLogMapper;
import com.lunar.system.service.OperLogService;
import com.lunar.system.service.OperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OperLogServiceImpl extends BaseServiceImpl<OperLogMapper, OperLog>
    implements OperLogService {

}