package by.vitikova.discovery.interseptor;

//todo это нужно убрать когда будет нормальная сага
///**
// * Перехватчик для Feign клиента для добавления заголовка Authorization в исходящие запросы.
// */
//@Component
//public class FeignClientInterceptor implements RequestInterceptor {
//
//    private static final Logger logger = LoggerFactory.getLogger(FeignClientInterceptor.class);
//
//    public void apply(RequestTemplate template) {
//        if (RequestContextHolder.getRequestAttributes() != null && RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String authorization = request.getHeader("Authorization");
//            logger.info("Authorization: " + authorization);
//            if (StringUtils.isNotEmpty(authorization)) {
//                template.header("Authorization", authorization);
//            }
//        }
//    }
//}