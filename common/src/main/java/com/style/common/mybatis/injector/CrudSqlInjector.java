package com.style.common.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.style.common.mybatis.injector.method.*;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CrudSqlInjector extends AbstractSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList() {

        return Stream.of(
                new Insert(),
                new Delete(),
                new DeleteById(),
                new DeleteByIds(),
                new Update(),
                new UpdateById(),
                new SelectById(),
                new SelectPage(),
                new SelectList()

        ).collect(toList());
    }
}
