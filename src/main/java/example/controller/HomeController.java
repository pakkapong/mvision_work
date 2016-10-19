package example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.Model.Customer;
import example.common.CommonService;
import example.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
public class HomeController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @RequestMapping("/")
    public String home(Model model) {

        return "index";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer",customer);

        return "register";
    }

    @RequestMapping("/authentication")
    public String authentication(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer",customer);

        return "authentication";

    }

    @RequestMapping(value = "/save" , method = RequestMethod.POST)
    public String save(Customer customer,Model model) {
        try {

            boolean isDuplicate = customerRepository.exists(customer.getUsername());
            if(!isDuplicate) {
                CommonService commonService = new CommonService();
                String sha1 = commonService.convertStringToSha1(customer.getPassword());
                customer.setPassword(sha1);
                customerRepository.save(customer);

            }
            else
            {
                model.addAttribute("error_message","key is duplicate");
                return "register";
            }
        }
        catch (Exception ex)
        {
            model.addAttribute("error_message","can t save data to database");
        }

        return "redirect:" ;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Boolean login(Customer customer , Model model,HttpServletResponse response) {
        Customer temp_customer = customerRepository.findOne(customer.getUsername());
        CommonService commonService = new CommonService();
        String password_sha1 = commonService.convertStringToSha1(customer.getPassword());
        if(temp_customer != null)
        {
            if(temp_customer.getPassword().equals(password_sha1))
            {
                String uniqueID = UUID.randomUUID().toString();
                String jsonStr = commonService.convertCustomerToJson(temp_customer);

                redisTemplate.opsForValue().set(uniqueID,jsonStr);
                redisTemplate.expire(uniqueID,60, TimeUnit.SECONDS);
                response.setHeader("session_id",uniqueID);

                model.addAttribute("test1",uniqueID);
                model.addAttribute("test2",jsonStr);

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @RequestMapping(value = "memberinfo")
    public String checkSessionId() {
        return "member_info";
    }

    @RequestMapping(value = "/check_session",method = RequestMethod.POST)
    @ResponseBody
    public Boolean checkSessionId(String session_id) {
        String key = redisTemplate.opsForValue().get(session_id,0,100);
        Boolean hasKey = false;
        if(!key.isEmpty())
        {
            hasKey = true;
        }
        return hasKey;
    }

}
