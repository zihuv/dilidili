package com.zihuv.dilidili.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.dilidili.exception.ClientException;
import com.zihuv.dilidili.mapper.CollectMapper;
import com.zihuv.dilidili.model.entity.Collect;
import com.zihuv.dilidili.model.entity.CollectRelation;
import com.zihuv.dilidili.model.entity.Video;
import com.zihuv.dilidili.model.param.CollectParam;
import com.zihuv.dilidili.model.vo.CollectVO;
import com.zihuv.dilidili.model.vo.CollectVideoVO;
import com.zihuv.dilidili.service.CollectRelationService;
import com.zihuv.dilidili.service.CollectService;
import com.zihuv.dilidili.service.VideoService;
import com.zihuv.dilidili.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {

    @Autowired
    private CollectRelationService collectRelationService;

    @Autowired
    private VideoService videoService;

    @Override
    public void createCollect(String collectName) {
        LambdaQueryWrapper<Collect> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Collect::getCollectName, collectName);
        List<Collect> collectList = this.list(lqw);
        if (CollUtil.isNotEmpty(collectList)) {
            throw new ClientException(StrUtil.format("[收藏服务] 收藏夹：{} 已经存在", collectName));
        }

        Collect collect = new Collect();
        collect.setUserId(UserContext.getUserId());
        collect.setCollectName(collectName);
        this.save(collect);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void collectVideo(CollectParam collectParam) {
        // 可以一次性添加到多个收藏夹
        // step1.校验视频和收藏夹是否存在
        if (videoService.getById(collectParam.getVideoId()) == null) {
            throw new ClientException(StrUtil.format("[收藏服务] 视频：{} 不存在", collectParam.getVideoId()));
        }
        LambdaQueryWrapper<Collect> lqwCollect = new LambdaQueryWrapper<>();
        lqwCollect.in(Collect::getId, collectParam.getCollectIdList());
        List<Collect> collectList = this.list(lqwCollect);
        if (CollUtil.isEmpty(collectList) || collectList.size() != collectParam.getCollectIdList().size()) {
            throw new ClientException("[收藏服务] 所选收藏夹不存在");
        }
        // step2.查询该收藏夹是否已经有该视频
        LambdaQueryWrapper<CollectRelation> lqwRelation = new LambdaQueryWrapper<>();
        lqwRelation.eq(CollectRelation::getVideoId, collectParam.getVideoId());
        lqwRelation.in(CollectRelation::getCollectId, collectParam.getCollectIdList());
        lqwRelation.select(CollectRelation::getId);
        List<CollectRelation> collectRelationList = collectRelationService.list(lqwRelation);
        if (CollUtil.isNotEmpty(collectRelationList)) {
            throw new ClientException("[收藏服务] 收藏夹已经收藏了该视频");
        }
        // step3.加收藏视频记录添加进数据库
        for (Long collectId : collectParam.getCollectIdList()) {
            CollectRelation collectRelation = new CollectRelation();
            collectRelation.setCollectId(collectId);
            collectRelation.setVideoId(collectParam.getVideoId());
            collectRelation.setUserId(UserContext.getUserId());
            collectRelationService.save(collectRelation);
        }
    }

    @Override
    public List<?> listCollect() {
        // step1.查询出所有收藏夹列表
        LambdaQueryWrapper<Collect> lqwCollect = new LambdaQueryWrapper<>();
        lqwCollect.eq(Collect::getUserId,UserContext.getUserId());
        List<Collect> collectList = this.list(lqwCollect);
        // step2.查询出所有收藏的视频
        LambdaQueryWrapper<CollectRelation> lqwRelation = new LambdaQueryWrapper<>();
        lqwRelation.eq(CollectRelation::getUserId,UserContext.getUserId());
        List<CollectRelation> collectRelationList = collectRelationService.list(lqwRelation);
        Set<Long> videoIds = collectRelationList.stream().map(CollectRelation::getVideoId).collect(Collectors.toSet());
        // step3.根据收藏的视频 id 查询视频信息
        List<Video> videoList = videoService.listByIds(videoIds);
        // step4.将收藏夹列表和收藏视频合并
        List<CollectVO> result = new ArrayList<>();
        for (Collect collect : collectList) {
            List<CollectVideoVO> collectVideoVOList = new ArrayList<>();
            for (CollectRelation collectRelation : collectRelationList) {
                if (collect.getId().equals(collectRelation.getCollectId())) {
                    CollectVideoVO collectVideoVO = new CollectVideoVO();
                    collectVideoVO.setVideoId(collectRelation.getVideoId());
                    for (Video video : videoList) {
                        if (video.getId().equals(collectRelation.getVideoId())) {
                            collectVideoVO.setVideoName(video.getVideoName());
                            break;
                        }
                    }
                    collectVideoVOList.add(collectVideoVO);
                }
            }
            CollectVO collectVO = new CollectVO();
            collectVO.setCollectId(collect.getId());
            collectVO.setCollectName(collect.getCollectName());
            collectVO.setCollectVideoVOList(collectVideoVOList);
            result.add(collectVO);
        }
        return result;
    }

}