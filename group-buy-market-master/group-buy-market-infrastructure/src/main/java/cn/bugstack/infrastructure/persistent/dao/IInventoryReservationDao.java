package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.InventoryReservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IInventoryReservationDao {

    int insert(InventoryReservation inventoryReservation);


}
