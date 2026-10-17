<%
    // Forward legacy registration requests to AuthServlet securely
    request.getRequestDispatcher("/auth/register").forward(request, response);
%>