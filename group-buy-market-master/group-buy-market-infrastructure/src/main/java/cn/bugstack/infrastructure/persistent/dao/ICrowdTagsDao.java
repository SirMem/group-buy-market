package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.CrowdTags;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author needyou
 * @description 人群标签
 * @create 2024-12-28 11:49
 */
@Mapper
public interface ICrowdTagsDao {

    void updateCrowdTagsStatistics(CrowdTags crowdTagsReq);

}
