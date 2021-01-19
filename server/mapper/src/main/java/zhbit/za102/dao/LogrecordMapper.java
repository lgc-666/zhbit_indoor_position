package zhbit.za102.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import zhbit.za102.bean.Logrecord;
import zhbit.za102.bean.LogrecordExample;
@Component
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

    List<String> findAllLogrecord();
}