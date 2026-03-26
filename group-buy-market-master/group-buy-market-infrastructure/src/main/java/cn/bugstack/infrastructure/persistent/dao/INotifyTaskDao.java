package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.NotifyTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author needyou
 * @description 回调任务
 * @create 2025-01-26 18:23
 */
@Mapper
public interface INotifyTaskDao {

    void insert(NotifyTask notifyTask);

    List<NotifyTask> queryUnExecutedNotifyTaskList();

    NotifyTask queryUnExecutedNotifyTaskByTeamId(String teamId);

    int updateNotifyTaskStatusSuccess(NotifyTask notifyTask);

    int updateNotifyTaskStatusError(NotifyTask notifyTask);

    int updateNotifyTaskStatusRetry(NotifyTask notifyTask);

}
