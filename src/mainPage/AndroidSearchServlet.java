package mainPage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;

@WebServlet(name = "AndroidSearchServlet", urlPatterns = "/api/android-search")
public class AndroidSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        int limit = Integer.parseInt(request.getParameter("numOfMovies"));
        int offset = (Integer.parseInt(request.getParameter("page")) - 1) * limit;
        String sort = request.getParameter("sortby");
        
        JsonArray searchResults = SearchHelper.getSearchResult(query, sort, limit, offset, false);
        
        PrintWriter out = response.getWriter();
        out.write(searchResults.toString());
    }
}
