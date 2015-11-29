package dataclasses.web;

import org.apache.commons.codec.digest.DigestUtils;

public class Salt {
    public static void salting(User user) {
        String password = DigestUtils.sha1Hex(user.getPassword() + user.getSault());
        user.setPassword(password);
    }
}
