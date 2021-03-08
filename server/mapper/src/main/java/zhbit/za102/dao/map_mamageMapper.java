package zhbit.za102.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import zhbit.za102.bean.map_mamage;
import zhbit.za102.bean.map_mamageExample;
@Component
public interface map_mamageMapper {
    long countByExample(map_mamageExample example);

    int deleteByExample(map_mamageExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(map_mamage record);

    int insertSelective(map_mamage record);

    List<map_mamage> selectByExample(map_mamageExample example);

    map_mamage selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") map_mamage record, @Param("example") map_mamageExample example);

    int updateByExample(@Param("record") map_mamage record, @Param("example") map_mamageExample example);

    int updateByPrimaryKeySelective(map_mamage record);

    int updateByPrimaryKey(map_mamage record);

    List<String> findAllMap();
}