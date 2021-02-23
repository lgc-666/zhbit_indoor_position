package zhbit.za102.dao;

import java.sql.Timestamp;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import zhbit.za102.bean.*;

@Component
public interface VisitMapper {
    long countByExample(VisitExample example);

    int deleteByExample(VisitExample example);

    int deleteByPrimaryKey(Integer visitid);

    int insert(Visit record);

    int insertSelective(Visit record);

    List<Visit> selectByExample(VisitExample example);

    Visit selectByPrimaryKey(Integer visitid);

    int updateByExampleSelective(@Param("record") Visit record, @Param("example") VisitExample example);

    int updateByExample(@Param("record") Visit record, @Param("example") VisitExample example);

    int updateByPrimaryKeySelective(Visit record);

    int updateByPrimaryKey(Visit record);

    void updateCustomer(@Param("address")String address,@Param("mac")String mac, @Param("rssi")Integer rssi, @Param("first_in_time") Timestamp first_in_time, @Param("latest_in_time")Timestamp latest_in_time, @Param("beat")Timestamp beat, @Param("inJudge")Integer inJudge, @Param("visited_times")Integer visited_times, @Param("last_in_time")Timestamp last_in_time,@Param("rt")String rt);

    List<String> searchExtraJumpOut();

    void updateInjudge();
    void updateInjudge2(@Param("inJudge")Integer inJudge,@Param("mac")String mac,@Param("atAddress")String atAddress);

    void deleteExpiredCustomer();

    SumVisit getSumVisit(@Param("dateTime")String dateTime,@Param("address")String address);
    SumStoptime getSumStoptime(@Param("dateTime")String dateTime, @Param("address")String address);
    NowCount getsortNow(@Param("address")String address);
}