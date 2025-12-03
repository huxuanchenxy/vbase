package com.sescity.vbase.storager.dao;

import com.sescity.vbase.media.zlm.dto.MediaServerItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface MediaServerMapper {

    @Insert("INSERT INTO media_server (" +
            "id, " +
            "ip, " +
            "hookIp, " +
            "sdpIp, " +
            "streamIp, " +
            "httpPort, " +
            "httpSSlPort, " +
            "rtmpPort, " +
            "rtmpSSlPort, " +
            "rtpProxyPort, " +
            "rtspPort, " +
            "rtspSSLPort, " +
            "autoConfig, " +
            "secret, " +
            "rtpEnable, " +
            "rtpPortRange, " +
            "sendRtpPortRange, " +
            "recordAssistPort, " +
            "defaultServer, " +
            "createTime, " +
            "updateTime, " +
            "hookAliveInterval," +
            "vbaseId" +
            ") VALUES " +
            "(" +
            "'${id}', " +
            "'${ip}', " +
            "'${hookIp}', " +
            "'${sdpIp}', " +
            "'${streamIp}', " +
            "${httpPort}, " +
            "${httpSSlPort}, " +
            "${rtmpPort}, " +
            "${rtmpSSlPort}, " +
            "${rtpProxyPort}, " +
            "${rtspPort}, " +
            "${rtspSSLPort}, " +
            "${autoConfig}, " +
            "'${secret}', " +
            "${rtpEnable}, " +
            "'${rtpPortRange}', " +
            "'${sendRtpPortRange}', " +
            "${recordAssistPort}, " +
            "${defaultServer}, " +
            "'${createTime}', " +
            "'${updateTime}', " +
            "${hookAliveInterval}, " +
            "'${vbaseId}')")
    int add(MediaServerItem mediaServerItem);

    @Update(value = {" <script>" +
            "UPDATE media_server " +
            "SET updateTime='${updateTime}'" +
            "<if test=\"ip != null\">, ip='${ip}'</if>" +
            "<if test=\"hookIp != null\">, hookIp='${hookIp}'</if>" +
            "<if test=\"sdpIp != null\">, sdpIp='${sdpIp}'</if>" +
            "<if test=\"streamIp != null\">, streamIp='${streamIp}'</if>" +
            "<if test=\"httpPort != null\">, httpPort=${httpPort}</if>" +
            "<if test=\"httpSSlPort != null\">, httpSSlPort=${httpSSlPort}</if>" +
            "<if test=\"rtmpPort != null\">, rtmpPort=${rtmpPort}</if>" +
            "<if test=\"rtmpSSlPort != null\">, rtmpSSlPort=${rtmpSSlPort}</if>" +
            "<if test=\"rtpProxyPort != null\">, rtpProxyPort=${rtpProxyPort}</if>" +
            "<if test=\"rtspPort != null\">, rtspPort=${rtspPort}</if>" +
            "<if test=\"rtspSSLPort != null\">, rtspSSLPort=${rtspSSLPort}</if>" +
            "<if test=\"autoConfig != null\">, autoConfig=${autoConfig}</if>" +
            "<if test=\"rtpEnable != null\">, rtpEnable=${rtpEnable}</if>" +
            "<if test=\"rtpPortRange != null\">, rtpPortRange='${rtpPortRange}'</if>" +
            "<if test=\"sendRtpPortRange != null\">, sendRtpPortRange='${sendRtpPortRange}'</if>" +
            "<if test=\"secret != null\">, secret='${secret}'</if>" +
            "<if test=\"recordAssistPort != null\">, recordAssistPort=${recordAssistPort}</if>" +
            "<if test=\"hookAliveInterval != null\">, hookAliveInterval=${hookAliveInterval}</if>" +
            "<if test=\"vbaseId != null\">, vbaseId='${vbaseId}'</if>" +
            "WHERE id='${id}'"+
            " </script>"})
    int update(MediaServerItem mediaServerItem);

    @Update(value = {" <script>" +
            "UPDATE media_server " +
            "SET updateTime='${updateTime}'" +
            "<if test=\"id != null\">, id='${id}'</if>" +
            "<if test=\"hookIp != null\">, hookIp='${hookIp}'</if>" +
            "<if test=\"sdpIp != null\">, sdpIp='${sdpIp}'</if>" +
            "<if test=\"streamIp != null\">, streamIp='${streamIp}'</if>" +
            "<if test=\"httpSSlPort != null\">, httpSSlPort=${httpSSlPort}</if>" +
            "<if test=\"rtmpPort != null\">, rtmpPort=${rtmpPort}</if>" +
            "<if test=\"rtmpSSlPort != null\">, rtmpSSlPort=${rtmpSSlPort}</if>" +
            "<if test=\"rtpProxyPort != null\">, rtpProxyPort=${rtpProxyPort}</if>" +
            "<if test=\"rtspPort != null\">, rtspPort=${rtspPort}</if>" +
            "<if test=\"rtspSSLPort != null\">, rtspSSLPort=${rtspSSLPort}</if>" +
            "<if test=\"autoConfig != null\">, autoConfig=${autoConfig}</if>" +
            "<if test=\"rtpEnable != null\">, rtpEnable=${rtpEnable}</if>" +
            "<if test=\"rtpPortRange != null\">, rtpPortRange='${rtpPortRange}'</if>" +
            "<if test=\"sendRtpPortRange != null\">, sendRtpPortRange='${sendRtpPortRange}'</if>" +
            "<if test=\"secret != null\">, secret='${secret}'</if>" +
            "<if test=\"recordAssistPort != null\">, recordAssistPort=${recordAssistPort}</if>" +
            "<if test=\"hookAliveInterval != null\">, hookAliveInterval=${hookAliveInterval}</if>" +
            "<if test=\"vbaseId != null\">, vbaseId='${vbaseId}'</if>" +
            "WHERE ip='${ip}' and httpPort=${httpPort}"+
            " </script>"})
    int updateByHostAndPort(MediaServerItem mediaServerItem);

    @Select("SELECT * FROM media_server WHERE id='${id}' and vbaseId='${vbaseId}'")
    MediaServerItem queryOne(String id, String vbaseId);

    @Select("SELECT * FROM media_server WHERE vbaseId='${vbaseId}'")
    List<MediaServerItem> queryAll(String vbaseId);

    @Delete("DELETE FROM media_server WHERE id='${id}' and vbaseId='${vbaseId}'")
    void delOne(String id, String vbaseId);

    @Select("DELETE FROM media_server WHERE ip='${host}' and httpPort=${port} and vbaseId='${vbaseId}'")
    void delOneByIPAndPort(String host, int port, String vbaseId);

    @Delete("DELETE FROM media_server WHERE defaultServer=1 and vbaseId='${vbaseId}'")
    int delDefault(String vbaseId);

    @Select("SELECT * FROM media_server WHERE ip='${host}' and httpPort=${port} and vbaseId='${vbaseId}'")
    MediaServerItem queryOneByHostAndPort(String host, int port, String vbaseId);

    @Select("SELECT * FROM media_server WHERE defaultServer=1 and vbaseId='${vbaseId}'")
    MediaServerItem queryDefault(String vbaseId);
}
