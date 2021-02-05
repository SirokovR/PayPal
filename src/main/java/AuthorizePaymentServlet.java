import com.paypal.base.rest.PayPalRESTException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/authorize_payment")
public class AuthorizePaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public AuthorizePaymentServlet(){

    }

    protected void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String product = request.getParameter("product");
        String subtotal = request.getParameter("subtotal");
        String shipping = request.getParameter("shipping");
        String tax = request.getParameter("tax");
        String total = request.getParameter("total");

        OrderDetail orderDetail = new OrderDetail(product,subtotal,shipping,tax,total);
        try {
            PaymentServices paymentServices = new PaymentServices();
            String approvalLink = paymentServices.authorizePayment(orderDetail);

            response.sendRedirect(approvalLink);

        } catch (PayPalRESTException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ex.printStackTrace();
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }

    }
}
