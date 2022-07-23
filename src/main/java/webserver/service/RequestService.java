package webserver.service;

import db.DataBase;
import exception.BadRequestException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.Cookie;
import webserver.request.Request;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestService {
    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
    private static final String ALREADY_EXIST_USER = "이미 존재하는 회원입니다.";

    private static final String COOKIE_LOGINED_SUCCESS_VALUE = "true";
    private static final String COOKIE_LOGINED_FAIL_VALUE = "false";

    private final Request request;

    public RequestService(Request request) {
        this.request = request;
    }

    public String saveMember() {

        User user = request.convertUserOfQueryParam();
        validate(user);
        DataBase.addUser(user);
        logger.debug("회원 저장 성공: {}", user.getUserId());
        return user.getUserId();
    }

    private void validate(User user) {
        if (Objects.nonNull(findByUserId(user.getUserId()))) {
            throw new BadRequestException(ALREADY_EXIST_USER);
        }
    }

    public User findByUserId(String userId) {
        return DataBase.findUserById(userId);
    }

    public Cookie checkIdAndPassword(User requestUser) {
        logger.debug("webserver.request user : {}", requestUser);
        User savedUser = findByUserId(requestUser.getUserId());
        Cookie cookie = new Cookie();
        if (Objects.isNull(savedUser) ||
                !savedUser.getPassword().equals(requestUser.getPassword())) {
            logger.debug("cookie 로그인 실패");
            return cookie.setCookie(Cookie.LOGINED_KEY, COOKIE_LOGINED_FAIL_VALUE);
        }

        logger.debug("cookie 로그인 성공");
        return cookie.setCookie(Cookie.LOGINED_KEY, COOKIE_LOGINED_SUCCESS_VALUE);
    }

    public List<User> findAllUser() {
        return DataBase.findAll()
                .stream()
                .map(user -> User.builder()
                        .userId(user.getUserId())
                        .password(user.getPassword())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build()
                )
                .collect(Collectors.toList());
    }
}