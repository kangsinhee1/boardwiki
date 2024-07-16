package kr.spring.chat.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import kr.spring.chat.vo.ChatMemberVO;
import kr.spring.chat.vo.ChatRoomVO;
import kr.spring.chat.vo.ChatTextVO;


@Mapper
public interface ChatMapper {

		//채팅방 목록
		public List<ChatRoomVO> selectChatRoomList(Map<String,Object>map);
		public Integer selectRowCount(Map<String,Object>map);
		//채팅방 번호 생성
		@Select("SELECT chat_room_seq.nextval FROM dual")
		public Integer selectChatRoomNum();
		//채팅방 생성
		@Insert("Insert INTO chat_room (chaR_num,chaR_name,tea_num) VALUES(#{chaR_num},#{chaR_name},#{tea_num})")
		public void insertChatRoom (ChatRoomVO chatRoomVO);
		//채팅방 멤버 등록
		public void insertChatRoomMember(@Param(value="chaR_num")Long chaR_num,@Param(value="chaR_name")String chaR_name,@Param(value="mem_num")Long mem_num);
		//채팅방 멤버 읽기
		public List<ChatMemberVO> selectChatMember(Long chaR_num);
		//채팅 메시지 번호 생성
		public Integer selectChatNum();
		//채팅 메시지 등록
		public void insertChat(ChatTextVO chatTextVO);
		//채팅 메시지 읽기
		public List<ChatTextVO> selectChatTextDetail(Map<String,Long>map);
		//읽은 채팅 기록 삭제
		public void deleteChatRead(Map<String,Long>map);
		//채팅 생성시 안읽은 메시지 식별 위한 chat_read생성
		public void insertChatRead(@Param(value="chaR_num")Long chaR_num,@Param(value="chaT_num")Long chaT_num,@Param(value="mem_num")Long mem_num);
		//채팅 메시지 한건 불러오기
		
}

