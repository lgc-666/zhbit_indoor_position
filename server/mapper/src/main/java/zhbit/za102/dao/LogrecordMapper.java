package test.school.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import test.school.bean.Logrecord;
import test.school.bean.LogrecordExample;

public interface LogrecordMapper {
    long countByExample(LogrecordExample example);

    int deleteByExample(LogrecordExample example);

    int deleteByPrimaryKey(Integer logid);

    int insert(Logrecord record);

    int insertSelective(Logrecord record);

    List<Logrecord> selectByExample(LogrecordExample example);

    Logrecord selectByPrimaryKey(Integer logid);

    int updateByExampleSelective(@Param("record") Logrecord record, @Param("example") LogrecordExample example);

    int updateByExample(@Param("record") Logrecord record, @Param("example") LogrecordExample example);

    int updateByPrimaryKeySelective(Logrecord record);

    int updateByPrimaryKey(Logrecord record);
}