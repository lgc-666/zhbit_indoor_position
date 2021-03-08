package zhbit.za102.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.DeviceExample;
@Component
public interface DeviceMapper {
    long countByExample(DeviceExample example);

    int deleteByExample(DeviceExample example);

    int deleteByPrimaryKey(Integer deviceid);

    int insert(Device record);

    int insertSelective(Device record);

    List<Device> selectByExample(DeviceExample example);

    Device selectByPrimaryKey(Integer deviceid);

    int updateByExampleSelective(@Param("record") Device record, @Param("example") DeviceExample example);

    int updateByExample(@Param("record") Device record, @Param("example") DeviceExample example);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);

    void updatebyid(@Param("deviceid")String deviceid,@Param("devicetype")String devicetype,@Param("devicevalue")String devicevalue,@Param("lasttime")String lasttime,@Param("ip")String ip,@Param("port")Integer port,@Param("gentime")String gentime);
    void insertdevice(@Param("deviceid")String deviceid,@Param("devicetype")String devicetype,@Param("devicevalue")String devicevalue,@Param("lasttime")String lasttime,@Param("ip")String ip,@Param("port")Integer port,@Param("gentime")String gentime,@Param("indoorname")String indoorname);

}