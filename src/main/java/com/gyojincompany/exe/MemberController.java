package com.gyojincompany.exe;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class MemberController
 */
@WebServlet("*.do")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MemberController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doAction(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doAction(request, response);
	}
	
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String uri = request.getRequestURI();
		String conPath = request.getContextPath();//컨택스트 패스만 분리하여 저장
		String command = uri.substring(conPath.length());
		
		System.out.println("command : "+command);
		
		String viewPage = null;
		
		if(command.equals("/main.do")) {
			
			viewPage = "/main.jsp";
			
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");
//			dispatcher.forward(request, response);
			
		} else if(command.equals("/login.do")) {
			
			viewPage = "/login.jsp";
			
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
//			dispatcher.forward(request, response);
		} else if(command.equals("/joinMember.do")) {
			
			viewPage = "/join.jsp";
			
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/join.jsp");
//			dispatcher.forward(request, response);
		} else if(command.equals("/modifyInfo.do")) {
			
			HttpSession session = request.getSession();
			
			String sessionId = (String) session.getAttribute("memberId");
			
			MemberDao dao = new MemberDao();
			MemberDto dto = dao.getMemberInfo(sessionId);
			
			request.setAttribute("memberDto", dto);
			
			viewPage = "/modify.jsp";
			
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/join.jsp");
//			dispatcher.forward(request, response);
		} else if(command.equals("/joinMemberOk.do")) {
			
			request.setCharacterEncoding("utf-8");//한글 처리
			
			String mid = request.getParameter("id");
			String mpw = request.getParameter("pw");
			String mname = request.getParameter("name");
			String memail = request.getParameter("email");
			
			int resultInt = 0;
			
			MemberDao dao = new MemberDao();
			
			int checkId = dao.checkId(mid);//1이면 가입하려는 아이디가 이미 존재
			
			if(checkId == 0) {
				resultInt = dao.joinMember(mid, mpw, mname, memail);//1이면 삽입 성공
			} else {
				System.out.println("가입하려는 아이디가 이미 존재합니다.");
				response.sendRedirect("joinMember.do");
			}
			
			if(resultInt == 1) {
				System.out.println("가입 성공!!!");
				viewPage = "/login.jsp";
			} else {
				viewPage = "/join.jsp";
			}
		} else if(command.equals("/loginOk.do")) {
			
			request.setCharacterEncoding("utf-8");
			
			String mid = request.getParameter("id");
			String mpw = request.getParameter("pw");
			
			MemberDao dao = new MemberDao();
			
			int loginResult = dao.loginCheck(mid, mpw);//1이면 로그인 성공, 0이면 로그인 실패
			
			if(loginResult == 1) { //로그인 성공
				HttpSession session = request.getSession();
				
				session.setAttribute("memberId", mid);//로그인 성공->세션에 아이디 저장
				session.setAttribute("ValidSession", "yes");
				System.out.println("로그인 성공!!!");
				viewPage = "/main.jsp";
			} else {
				viewPage = "/login.jsp";
			}
			
		} else if(command.equals("/logout.do")) {
			HttpSession session = request.getSession();
			
			session.invalidate();//로그 아웃(세션 삭제)
			viewPage = "/login.jsp";
		} else if(command.equals("/delete.do")) {
			HttpSession session = request.getSession();
			
			String sessionId = (String) session.getAttribute("memberId");
			
			MemberDao dao = new MemberDao();
			int resultFlag = dao.delete(sessionId);
			//1이 반환되면 삭제 성공
			
			if(resultFlag == 1) {
				session.invalidate();//세션 삭제->로그아웃
				viewPage = "/login.jsp";
			} else {
				viewPage = "/main.jsp";
			}
			
		} else if(command.equals("/modifyOk.do")) {
			
			request.setCharacterEncoding("utf-8");
			
			String mid = request.getParameter("id");
			String mpw = request.getParameter("pw");
			String mname = request.getParameter("name");
			String memail = request.getParameter("email");
			
			MemberDao dao = new MemberDao();
			
			int resultFlag = dao.modify(mid, mpw, mname, memail);//수정성공이면 1
			
			if(resultFlag == 1) {
				MemberDto dto = dao.getMemberInfo(mid);
				request.setAttribute("memberDto", dto);
				viewPage = "/modifyOk.jsp";
				
			} else {//수정 실패했을 경우
				viewPage = "/modify.jsp";
			}
		} 
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
		dispatcher.forward(request, response);
	}

}
