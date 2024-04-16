package com.lunar.common.aliyun.ding;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.aliyun.dingtalkcalendar_1_0.models.ListEventsHeaders;
import com.aliyun.dingtalkcalendar_1_0.models.ListEventsRequest;
import com.aliyun.dingtalkcalendar_1_0.models.ListEventsResponse;
import com.aliyun.dingtalkcalendar_1_0.models.ListEventsResponseBody;
import com.aliyun.dingtalkcalendar_1_0.models.ListEventsResponseBody.ListEventsResponseBodyEvents;
import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkexclusive_1_0.models.GetConferenceDetailHeaders;
import com.aliyun.dingtalkexclusive_1_0.models.GetConferenceDetailResponse;
import com.aliyun.dingtalkexclusive_1_0.models.GetConferenceDetailResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.request.OapiKacDatavVideoconfDetailListRequest.McsSummaryRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.api.response.OapiHealthStepinfoListResponse.BasicStepInfoVo;
import com.dingtalk.api.response.OapiKacDatavVideoconfDetailListResponse.McsDetailResponse;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse.UserGetByUnionIdResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse.UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse.UserGetByCodeResponse;
import com.google.common.collect.Lists;
import com.lunar.common.aliyun.ding.input.DingActionCardMsgInput;
import com.lunar.common.aliyun.ding.input.DingOAMsgInput;
import com.lunar.common.aliyun.ding.output.*;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.utils.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author szx
 * @date 2022/06/20 11:33
 */
@Slf4j
public class DingHelper {

    /**
     * 获取钉钉accessToken
     *
     * @param appid
     * @param secret
     * @return
     */
    public static DingAccessToken getAccessToken(String appid, String secret) {
        log.info("获取钉钉accessToken");
        Assert.notBlank(appid, "appid cannot be null");
        Assert.notBlank(secret, "secret cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(appid);
            request.setAppsecret(secret);
            request.setHttpMethod("GET");
            OapiGettokenResponse resp = client.execute(request);
            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            if (resp == null || StringUtils.isBlank(resp.getAccessToken())) {
                throw new ServiceException(ThirdCode.DING_ACCESS_TOKEN_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0L) {
                throw new ServiceException(resp.getErrmsg());
            }

            return DingAccessToken.builder()
                .accessToken(resp.getAccessToken())
                .expiresIn(resp.getExpiresIn())
                .build();
        } catch (Exception e) {
            log.error("获取钉钉accessToken失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 查询钉钉用户信息
     *
     * @param accessToken
     * @param code
     * @return
     */
    public static DingUserInfo getUserInfo(String accessToken, String code) {
        log.info("查询钉钉用户信息. code={}", code);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(code, "code cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getuserinfo");
            OapiV2UserGetuserinfoRequest req = new OapiV2UserGetuserinfoRequest();
            req.setCode(code);
            OapiV2UserGetuserinfoResponse resp = client.execute(req, accessToken);
            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            if (resp == null || resp.getResult() == null) {
                throw new ServiceException(ThirdCode.DING_USER_INFO_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                throw new ServiceException(resp.getErrmsg());
            }

            UserGetByCodeResponse result = resp.getResult();
            return DingUserInfo.builder()
                .associatedUnionid(result.getAssociatedUnionid())
                .unionid(result.getUnionid())
                .name(result.getName())
                .userid(result.getUserid())
                .build();
        } catch (Exception e) {
            log.error("获取钉钉用户信息失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 查询钉钉用户详情
     *
     * @param accessToken
     * @param userid
     * @return
     */
    public static DingUserDetail getUserDetail(String accessToken, String userid) {
        log.info("查询钉钉用户详情. userid={}", userid);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(userid, "userid cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userid);
            req.setLanguage("zh_CN");
            OapiV2UserGetResponse resp = client.execute(req, accessToken);
            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            if (resp == null || resp.getResult() == null) {
                throw new ServiceException(ThirdCode.DING_USER_DETAIL_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                throw new ServiceException(resp.getErrmsg());
            }

            UserGetResponse result = resp.getResult();

            return DingUserDetail.builder()
                .unionid(result.getUnionid())
                .name(result.getName())
                .userid(result.getUserid())
                .avatar(result.getAvatar())
                .mobile(result.getMobile())
                .jobNumber(result.getJobNumber())
                .email(result.getEmail())
                .build();
        } catch (Exception e) {
            log.error("获取钉钉用户信息失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 获取钉钉今日步数
     *
     * @param accessToken
     * @param userid
     * @return
     */
    public static int getTodayStep(String accessToken, String userid) {
        log.info("获取钉钉今日步数. userid={}", userid);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(userid, "userid cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/health/stepinfo/list");
            OapiHealthStepinfoListRequest req = new OapiHealthStepinfoListRequest();
            req.setType(0L);
            req.setObjectId(userid);
            req.setStatDates(DateUtil.format(new Date(), "yyyyMMdd"));
            OapiHealthStepinfoListResponse resp = client.execute(req, accessToken);
            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            if (resp == null) {
                throw new ServiceException(ThirdCode.DING_STEP_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                throw new ServiceException(resp.getErrmsg());
            }

            // 返回成功，但stepinfo_list可能为空
            // {"code":null,"message":null,"errorCode":"0","msg":"","subCode":"","subMsg":"","subMessage":null,"flag":null,"requestId":null,"qimenType":null,"body":"{\"errcode\":0,\"stepinfo_list\":[],\"request_id\":\"15r5ohnvur7iy\"}","headerContent":null,"requestUrl":null,"params":{"stat_dates":"20220803","type":"0","object_id":"161513085629080863"},"errcode":0,"errmsg":null,"stepinfoList":[],"success":true}
            if (CollectionUtils.isEmpty(resp.getStepinfoList())) {
                return 0;
            }

            List<BasicStepInfoVo> stepinfoList = resp.getStepinfoList();
            return stepinfoList.get(0).getStepCount().intValue();
        } catch (Exception e) {
            log.error("获取钉钉用户步数失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkcalendar_1_0.Client createCalendarClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkcalendar_1_0.Client(config);
    }

    /**
     * 获取钉钉日程
     *
     * @param accessToken
     * @param unionid
     * @param timeMin
     * @param timeMax
     */
    public static List<ListEventsResponseBodyEvents> getCalendar(String accessToken, String unionid, Date timeMin,
                                                                 Date timeMax) {
        log.info("获取钉钉日程. unionid={}, timeMin={}, timeMax={}", unionid, timeMin, timeMax);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(unionid, "unionid cannot be null");

        try {
            com.aliyun.dingtalkcalendar_1_0.Client client = createCalendarClient();
            ListEventsHeaders listEventsHeaders = new ListEventsHeaders();
            listEventsHeaders.xAcsDingtalkAccessToken = accessToken;
            ListEventsRequest listEventsRequest = new ListEventsRequest()
                .setTimeMin(DateUtil.format(timeMin, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .setTimeMax(DateUtil.format(timeMax, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .setShowDeleted(false);
//                .setMaxResults(15)
//                .setMaxAttendees(10)
//                .setNextToken("cnNTbW1xxxxEgvdlQrQT09")
//                .setSyncToken("cnNTbW1YbxxxxdEgvdlQrQT09");

            ListEventsResponse resp = client.listEventsWithOptions(unionid, "primary", listEventsRequest,
                listEventsHeaders, new RuntimeOptions());

            if (resp == null) {
                throw new ServiceException(ThirdCode.DING_CALENDAR_FAILED);
            }

            ListEventsResponseBody body = resp.getBody();

            if (body == null || CollectionUtils.isEmpty(body.getEvents())) {
                log.info("用户无日程");
                return Lists.newArrayList();
            }

            List<ListEventsResponseBodyEvents> list = body.getEvents();
            log.info("钉钉日程查询结果size: {}", list.size());

            return list;
        } catch (Exception e) {
            log.error("获取钉钉日程数据失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 从日程中解析在线会议，按结束时间顺序
     *
     * @param accessToken
     * @param unionid
     * @param timeMin
     * @param timeMax
     */
    public static List<DingCalendarOutput> getMeetingFromCalendar(String accessToken, String unionid,
                                                                  Date timeMin, Date timeMax) {
        log.info("从日程中解析在线会议. accessToken={}, unionid={}, timeMin={}, timeMax={}", accessToken, unionid,
            timeMin,
            timeMax);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(unionid, "unionid cannot be null");

        List<ListEventsResponseBodyEvents> events = getCalendar(accessToken, unionid, timeMin, timeMax);
        if (CollectionUtils.isEmpty(events)) {
            return Lists.newArrayList();
        }

        // 只取视频会议数据
        List<DingCalendarOutput> outputList = events
            .stream()
            .filter(x -> ObjectUtils.allNotNull(x.getOnlineMeetingInfo(), x.getStart(), x.getEnd(),
                x.getStart().getDateTime(), x.getEnd().getDateTime()))
            .map(y -> {
                DingCalendarOutput output = DingCalendarOutput
                    .builder()
                    .calendarId(y.getId())
                    .summary(y.getSummary())
//                    .onlineMeetingInfo(true)
                    .build();
                try {
                    output.setStartTime(DateUtil.parse(y.getStart().getDateTime()).toJdkDate());
                    output.setEndTime(DateUtil.parse(y.getEnd().getDateTime()).toJdkDate());
                    // 日程时长
                    long duration = DateUtil.between(output.getStartTime(), output.getEndTime(), DateUnit.MINUTE, true);
                    output.setDuration((int) duration);
                } catch (Exception e) {
                    log.error("解析钉钉时间错误", e);
                }
                return output;
            })
            .sorted(Comparator.comparing(DingCalendarOutput::getEndTime))
            .collect(Collectors.toList());

        log.info("在线会议数据: {}", JsonHelper.toJson(outputList));

        return outputList;
    }

    /**
     * 获取企业视频会议明细列表
     *
     * @param accessToken
     * @param day         20200720
     * @param size        分页大小，不超过100
     * @param cursor
     */
    public static List<DingMeetingOutput> getMeetingList(String accessToken, String day, long size, long cursor) {
        log.info("获取企业视频会议明细列表. accessToken={}, day={}, size={}, cursor={}", accessToken, day, size,
            cursor);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(day, "day cannot be null");
        Assert.isTrue(size > 0, "size cannot be 0");

        try {
            DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/topapi/kac/datav/videoconf/detail/list");
            OapiKacDatavVideoconfDetailListRequest req = new OapiKacDatavVideoconfDetailListRequest();
            McsSummaryRequest obj = new McsSummaryRequest();
            obj.setDataId(day);
            obj.setSize(size);
            obj.setCursor(cursor);
            req.setRequest(obj);
            OapiKacDatavVideoconfDetailListResponse resp = client.execute(req, accessToken);

            if (resp == null) {
                log.error("钉钉返回数据为null");
                throw new ServiceException(ThirdCode.DING_MEETING_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                log.error("钉钉返回errcode={}, errmsg={}", resp.getErrcode(), resp.getErrmsg());
                throw new ServiceException(resp.getErrmsg());
            }

            log.info("钉钉返回={}", JsonHelper.toJson(resp.getResult()));

            List<DingMeetingOutput> list = Lists.newArrayList();

            McsDetailResponse result = resp.getResult();
            if (CollectionUtils.isEmpty(result.getData())) {
                return list;
            }

            list = result.getData().stream().map(x -> {
                DingMeetingOutput output = DingMeetingOutput.builder().confId(x.getConfId()).build();

                output.setConfLenMin(new BigDecimal(x.getConfLenMin()).intValue());
                output.setJoinUserCount(x.getJoinUserCount().intValue());
                output.setStartTime(DateUtil.parse(x.getStartTime()).toJdkDate());
                output.setEndTime(DateUtil.parse(x.getEndTime()).toJdkDate());

                output.setHasMore(result.getHasMore());
                output.setNextCursor(result.getNextCursor());
                return output;
            }).collect(Collectors.toList());

            return list;
        } catch (Exception e) {
            log.error("获取企业视频会议明细列表失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkexclusive_1_0.Client createExclusiveClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkexclusive_1_0.Client(config);
    }

    /**
     * 获取视频会议详情
     *
     * @param accessToken
     * @param confId
     * @return
     */
    public static List<DingMeetingUser> getMeetingUser(String accessToken, String confId) {
        log.info("获取视频会议用户列表. accessToken={}, confId={}", accessToken, confId);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(confId, "confId cannot be null");

        try {
            com.aliyun.dingtalkexclusive_1_0.Client client = createExclusiveClient();
            GetConferenceDetailHeaders getConferenceDetailHeaders = new GetConferenceDetailHeaders();
            getConferenceDetailHeaders.xAcsDingtalkAccessToken = accessToken;
            GetConferenceDetailResponse resp = client.getConferenceDetailWithOptions(confId,
                getConferenceDetailHeaders, new RuntimeOptions());
            if (resp == null) {
                log.error("钉钉返回数据为null");
                throw new ServiceException(ThirdCode.DING_MEETING_DETAIL_FAILED);
            }

            GetConferenceDetailResponseBody body = resp.getBody();

            log.info("钉钉返回={}", JsonHelper.toJson(body));

            List<DingMeetingUser> list = Lists.newArrayList();
            if (body == null || CollectionUtils.isEmpty(body.getMemberList())) {
                return list;
            }

            list = body.getMemberList().stream().map(x -> {
                DingMeetingUser detail = CopyUtil.copyObject(x, DingMeetingUser.class);
                detail.setConfId(confId);
                detail.setAttendDuration((int) (x.getAttendDuration() / 1000));
                return detail;
            }).collect(Collectors.toList());

            return list;
        } catch (Exception e) {
            log.error("获取视频会议用户列表失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 根据unionid获取用户userid
     *
     * @param accessToken
     * @param unionid
     * @return
     */
    public static String getByUnionid(String accessToken, String unionid) {
        log.info("根据unionid获取用户userid. accessToken={}, unionid={}", accessToken, unionid);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(unionid, "unionid cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
            OapiUserGetbyunionidRequest req = new OapiUserGetbyunionidRequest();
            req.setUnionid(unionid);
            OapiUserGetbyunionidResponse resp = client.execute(req, accessToken);
            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            if (resp == null || resp.getResult() == null) {
                throw new ServiceException(ThirdCode.DING_GET_USERID_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                throw new ServiceException(resp.getErrmsg());
            }

            UserGetByUnionIdResponse userGetByUnionIdResponse = resp.getResult();

            return Optional.ofNullable(userGetByUnionIdResponse).map(UserGetByUnionIdResponse::getUserid).orElse(null);
        } catch (Exception e) {
            log.error("根据unionid获取用户userid失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 登录第三方网站client
     *
     * @return
     * @throws Exception
     */
    public static com.aliyun.dingtalkoauth2_1_0.Client authClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    /**
     * 登录第三方网站回调
     *
     * @param appid
     * @param secret
     * @param authCode
     * @return
     */
    public static DingAccessToken authCodeCallback(String appid, String secret, String authCode) {
        log.info("登录第三方网站回调. appid={}, authCode={}", appid, authCode);
        Assert.notBlank(appid, "appid cannot be null");
        Assert.notBlank(secret, "secret cannot be null");
        Assert.notBlank(authCode, "authCode cannot be null");

        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = authClient();
            GetUserTokenRequest req = new GetUserTokenRequest()
                //应用基础信息-应用信息的AppKey,请务必替换为开发的应用AppKey
                .setClientId(appid)
                //应用基础信息-应用信息的AppSecret，,请务必替换为开发的应用AppSecret
                .setClientSecret(secret)
                .setCode(authCode)
                .setGrantType("authorization_code");
            GetUserTokenResponse resp = client.getUserToken(req);
            if (resp == null || resp.getBody() == null) {
                throw new ServiceException(ThirdCode.DING_AUTH_CODE_FAILED);
            }

            GetUserTokenResponseBody body = resp.getBody();

            log.info("钉钉返回={}", JsonHelper.toJson(body));

            return DingAccessToken.builder()
                .accessToken(body.getAccessToken())
                .expiresIn(body.getExpireIn())
                .build();
        } catch (Exception e) {
            log.error("登录第三方网站回调失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 通讯录client
     *
     * @return
     * @throws Exception
     */
    public static com.aliyun.dingtalkcontact_1_0.Client contactClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }

    /**
     * 根据第三方回调token获取用户信息
     *
     * @param userToken
     * @return
     */
    public static DingUserCallBack getUserinfoByToken(String userToken) {
        log.info("根据第三方回调token获取用户信息. userToken={}", userToken);
        Assert.notBlank(userToken, "userToken cannot be null");

        try {
            com.aliyun.dingtalkcontact_1_0.Client client = contactClient();
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = userToken;
            //获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
            // {"avatarUrl":"https://static-legacy.dingtalk.com/media/lQDPDhvDZuDuvA3NC9DNC9Cw43fo_KM_BOoDENHSE0B1AA_3024_3024.jpg","email":null,"mobile":"13011195171","nick":"rrr","openId":"oqdWiPcy5k0T180sr1rocIQiEiE","stateCode":"86","unionId":"2pcpyu6fRlFql5sSopwE3giEiE"}
            GetUserResponse resp = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            if (resp == null || resp.getBody() == null) {
                throw new ServiceException(ThirdCode.DING_GET_USER_BY_TOKEN_FAILED);
            }
            GetUserResponseBody body = resp.getBody();
            log.info("钉钉返回={}", JsonHelper.toJson(body));

            return CopyUtil.copyObject(body, DingUserCallBack.class);
        } catch (Exception e) {
            log.error("根据第三方回调token获取用户信息失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 钉钉发送工作通知-卡片消息-异步
     *
     * @param accessToken
     * @param agentId
     * @param useridList  用户id列表，","分割。最大用户列表长度100。
     * @param input
     * @return
     */
    public static DingMsgOutput sendActionCardMsgAsync(String accessToken, Long agentId, String useridList,
                                                       DingActionCardMsgInput input) {
        log.info("钉钉发送卡片消息工作通知. accessToken={}, input={}", accessToken, input);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notNull(agentId, "agentId cannot be null");
        Assert.notNull(useridList, "useridList cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(agentId);
            req.setUseridList(useridList);

            Msg msg = new Msg();
            msg.setMsgtype("action_card");

            ActionCard actionCard = new ActionCard();
            actionCard.setSingleUrl(input.getSingleUrl());
            actionCard.setSingleTitle(input.getSingleTitle());
            actionCard.setMarkdown(input.getMarkdown());
            actionCard.setTitle(input.getTitle());

            msg.setActionCard(actionCard);
            req.setMsg(msg);
            // {
            //	"errcode":0,
            //	"errmsg":"ok",
            //	"task_id":2682343961505,
            //	"request_id":"16kjodhy5hjlj"
            //}
            OapiMessageCorpconversationAsyncsendV2Response resp = client.execute(req, accessToken);
            if (resp == null) {
                throw new ServiceException(ThirdCode.DING_SEND_MSG_ACTION_CARD_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0L) {
                throw new ServiceException(resp.getErrmsg());
            }

            DingMsgOutput output = CopyUtil.copyObject(resp, DingMsgOutput.class);
            log.info("钉钉返回={}", JsonHelper.toJson(output));

            return output;
        } catch (Exception e) {
            log.error("钉钉发送卡片消息工作通知失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 获取工作通知消息的发送结果
     * <p>
     * 注意 通过接口发送工作通知，当接收人列表超过100人时，不支持调用该接口，否则系统会返回调用超时。
     *
     * @param accessToken
     * @param agentId
     * @param taskId
     * @return
     */
    public static String getSendResult(String accessToken, Long agentId, Long taskId) {
        log.info("获取工作通知消息的发送结果. accessToken={}, agentId={}, taskId={}", accessToken, agentId, taskId);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notNull(agentId, "agentId cannot be null");
        Assert.notNull(taskId, "taskId cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
            OapiMessageCorpconversationGetsendresultRequest req = new OapiMessageCorpconversationGetsendresultRequest();
            req.setAgentId(agentId);
            req.setTaskId(taskId);
            OapiMessageCorpconversationGetsendresultResponse resp = client.execute(req, accessToken);
            if (resp == null) {
                throw new ServiceException(ThirdCode.DING_SEND_RESULT_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0L) {
                throw new ServiceException(resp.getErrmsg());
            }

            log.info("钉钉返回={}", JsonHelper.toJson(resp));

            return "";
        } catch (Exception e) {
            log.error("获取工作通知消息的发送结果失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @param accessToken
     * @param agentId
     * @param useridList  用户id列表，","分割。最大用户列表长度100。
     * @param input
     * @return
     */
    public static DingMsgOutput sendOAMsgAsync(String accessToken, Long agentId, String useridList,
                                               DingOAMsgInput input) {
        log.info("钉钉发送oa消息工作通知. accessToken={}, input={}", accessToken, input);
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notNull(agentId, "agentId cannot be null");
        Assert.notNull(useridList, "useridList cannot be null");

        try {
            DingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(agentId);
            req.setUseridList(useridList);

            Msg msg = new Msg();
            msg.setMsgtype("oa");

            OA oa = new OA();
            Body body = new Body();

            List<Form> formList = new ArrayList<>();
            List<DingOAMsgInput.Body.Form> list = input.getBody().getFormList();
            list.forEach(x -> {
                Form form = new Form();
                form.setValue(x.getValue());
                form.setKey(x.getKey());
                formList.add(form);
            });

            body.setForm(formList);
            body.setTitle(input.getBody().getTitle());

            oa.setBody(body);

            Head head = new Head();
            // head.setBgcolor("FFBBBBBB");
            head.setBgcolor(input.getHead().getBgcolor());
            // 如果是发送工作通知消息，该参数会被替换为当前应用名称。
            head.setText(input.getHead().getText());
            oa.setHead(head);

            msg.setOa(oa);

            req.setMsg(msg);
            // {
            //	"errcode":0,
            //	"errmsg":"ok",
            //	"task_id":2682343961505,
            //	"request_id":"16kjodhy5hjlj"
            //}
            OapiMessageCorpconversationAsyncsendV2Response resp = client.execute(req, accessToken);
            if (resp == null) {
                throw new ServiceException(ThirdCode.DING_SEND_MSG_ACTION_CARD_FAILED);
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0L) {
                throw new ServiceException(resp.getErrmsg());
            }

            DingMsgOutput output = CopyUtil.copyObject(resp, DingMsgOutput.class);
            log.info("钉钉返回={}", JsonHelper.toJson(output));

            return output;
        } catch (Exception e) {
            log.error("钉钉发送oa消息工作通知失败", e);
            throw new ServiceException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String accessToken = "4bc84e3833753020b638d7922f965397";
        Long agentId = 1774275110L;
        String userIdList = "manager8421,0727151962694168,1901464124841017";

        // http://mind-c-dev.xxx.net/?corpId=ding5627ca703565a2d2bc961a6cb783455b#/activity/detail?id=41

        DingActionCardMsgInput input = DingActionCardMsgInput.builder()
            .singleUrl(
                "http://c-dev.xxx.net/?corpId=ding5627ca703565a2d2bc961a6cb783455b#/activity")
            .singleTitle("跳转到碳账户应用")
            .title("碳账户活动通知")
            .markdown("记录电视剧法律手段咖啡机老大生发剂老大东方巨龙")
            .build();
        DingMsgOutput output = sendActionCardMsgAsync(accessToken, agentId, userIdList, input);
        System.out.println(output);

    }

}
