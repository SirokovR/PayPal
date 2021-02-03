import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.ArrayList;
import java.util.List;

public class PaymentServices {
    private static final String CLIENT_ID = "AeMrYO6xP9Ae0sbPZB2XyfWneGc1KiFv-7xaKnMf8k7ChTqgBcu0cwADhzFE-WvyShRWJEnbsomrI8Gg";
    private static final String CLIENT_SECRET = "EAe4SXfnGFQmsL5pugbWLQjcAUj13OiAZRwegRPx-WBnf8rf4guXl_TM-sheGjmGSpUBy1jmAWTX1DnV";
    private static final String MODE = "sandbox";

    public String authorizePayment(OrderDetail orderDetail)
            throws PayPalRESTException {

        Payer payer = getPayerInformation();
        RedirectUrls redirectUrls = getRedirectURLs();
        List<Transaction> listTransaction = getTransactionInformation(orderDetail);

        Payment requestPayment = new Payment();
        requestPayment.setTransactions(listTransaction);
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("authorize");

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        Payment approvedPayment = requestPayment.create(apiContext);

        return getApprovalLink(approvedPayment);

    }

    private Payer getPayerInformation() {
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        PayerInfo payerInfo =  new PayerInfo();
        payerInfo.setFirstName("Roman")
                .setLastName("Tati")
                .setEmail("tatitakk@gmail.com");
        payer.setPayerInfo(payerInfo);
        return payer;
    }

    private RedirectUrls getRedirectURLs() {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8088/PayPal_war/cancel.html");
        redirectUrls.setReturnUrl("http://localhost:8088/PayPal_war/review_payment");
        return redirectUrls;
    }

    private List<Transaction> getTransactionInformation(OrderDetail orderDetail) {
        Details details = new Details();
        details.setShipping(orderDetail.getShipping());
        details.setSubtotal(orderDetail.getSubtotal());
        details.setTax(orderDetail.getTax());

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(orderDetail.getTotal());
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(orderDetail.getProductName());

        ItemList itemList =  new ItemList();
        List<Item> items = new ArrayList<Item>();

        Item item = new Item();
        item.setCurrency("USD")
                .setCurrency(orderDetail.getProductName())
                .setPrice(orderDetail.getSubtotal())
                .setTax(orderDetail.getTax())
                .setQuantity("1");

        items.add(item);
        itemList.setItems(items);
        transaction.setItemList(itemList);

        List<Transaction> listTransaction = new ArrayList<Transaction>();
        listTransaction.add(transaction);

        return  listTransaction;
    }

    private String getApprovalLink(Payment approvedPayment) {
        List<Links> links = approvedPayment.getLinks();
        String approvalLink = null;
        for (Links link: links){
            if (link.getRel().equalsIgnoreCase("approval_url")){
                approvalLink = link.getHref();

            }
        }
        return approvalLink;
    }
}
