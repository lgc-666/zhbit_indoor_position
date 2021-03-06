package zhbit.za102.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import zhbit.za102.bean.*;

@Component
public interface ClassDataMapper {
    long countByExample(ClassDataExample example);

    int deleteByExample(ClassDataExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ClassData record);

    int insertSelective(ClassData record);

    List<ClassData> selectByExample(ClassDataExample example);

    ClassData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ClassData record, @Param("example") ClassDataExample example);

    int updateByExample(@Param("record") ClassData record, @Param("example") ClassDataExample example);

    int updateByPrimaryKeySelective(ClassData record);

    int updateByPrimaryKey(ClassData record);


    Integer selectWithin1hour();
    Integer selectWithin1hourByClass();
    void updateWithin1hour(@Param("hours")Integer hours,@Param("num")Integer num);
    void insertClassData (@Param("address")String address,@Param("hours")Integer hours,@Param("indoorname")String indoorname);
    void updateClassData(@Param("address")String address,@Param("newStudent")Integer newStudent,@Param("inClassNumber")Integer inClassNumber,@Param("hourInClassNumber")Integer hourInClassNumber,@Param("indoorname")String indoorname);

    Integer searchNowHour_in_customer_number(@Param("address")String address,@Param("indoorname")String indoorname);

    void updateDataThread(@Param("address") String address,@Param("dynamic_customer")Integer dynamic_customer,@Param("jumpOut_customer")Integer jumpOut_customer,@Param("subHour_customer")Integer subHour_customer,@Param("indoorname")String indoorname);

    void updateExtraJumpOut(@Param("address")String address,@Param("indoorname")String indoorname);
    void deleteExpiredShop_data();


    List<ClassData> getMainData(@Param("address") String address, @Param("dateTime") String dateTime,@Param("indoorname")String indoorname);
    List<CustomerPerHour> getCustomerPerHour(@Param("address")String address, @Param("dateTime")String dateTime,@Param("indoorname")String indoorname);
    List<InCustomerPerHour> getInCustomerPerHour(@Param("address")String address, @Param("dateTime")String dateTime,@Param("indoorname")String indoorname);
    List<SumData> getSum(@Param("dateTime")String dateTime,@Param("indoorname")String indoorname);

    Integer listdis();

    List<ClassData> getPreHour(@Param("prehour") Integer prehour);
    void updateClassDataTwo(@Param("address")String address,@Param("Number")Integer Number,@Param("indoorname")String indoorname);

}