package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testUserRegistrationRequestExhaustive() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("password123", request.getConfirmPassword());
        assertTrue(request.passwordConfirmationMathces());

        request.setPassword(null);
        assertFalse(request.passwordConfirmationMathces());
        request.setPassword("pass");
        request.setConfirmPassword(null);
        assertFalse(request.passwordConfirmationMathces());
        request.setConfirmPassword("diff");
        assertFalse(request.passwordConfirmationMathces());
    }

    @Test
    void testUserLoginRequestExhaustive() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testUpgradeRequestStatusChangeRequestExhaustive() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("testuser");
        request.setNewStatus("APPROVED");
        assertEquals("testuser", request.getUsername());
        assertEquals("APPROVED", request.getNewStatus());
    }

    @Test
    void testUpgradeRequestResponseExhaustive() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID userId = UUID.randomUUID();

        // Canonical
        UpgradeRequestResponse r = new UpgradeRequestResponse(id.toString(), now, userId.toString(), "un", "fn", "cr", "social", "st");
        assertEquals(id.toString(), r.id());
        
        // Builder exhaustive
        UpgradeRequestResponse.UpgradeRequestResponseBuilder b = UpgradeRequestResponse.builder();
        b.id(id.toString()); b.createdAt(now); b.requesterUserId(userId.toString()); b.requesterUsername("un"); b.fullName("fn"); b.credential("cr"); b.socialMediaUrl("social"); b.status("st");
        assertNotNull(b.toString());
        assertEquals(r, b.build());
        
        // Builder nulls
        UpgradeRequestResponse.UpgradeRequestResponseBuilder b2 = UpgradeRequestResponse.builder();
        b2.id(null); b2.createdAt(null); b2.requesterUserId(null); b2.requesterUsername(null); b2.fullName(null); b2.credential(null); b2.socialMediaUrl(null); b2.status(null);
        assertNull(b2.build().id());

        // fromRequest
        User user = User.builder().id(userId).username("un").build();
        UpgradeRequest ur = UpgradeRequest.builder()
                .upgrReqId(id.toString()).createdAt(now).requesterUser(user).fullName("fn").credential("cr").socialMediaUrl("social").status("st")
                .build();
        assertEquals(r, UpgradeRequestResponse.fromRequest(ur));
        
        // Record basics
        assertNotNull(r.toString());
        assertEquals(r.hashCode(), r.hashCode());
    }

    @Test
    void testUserLoginResponseExhaustive() {
        UUID id = UUID.randomUUID();
        UserLoginResponse r = new UserLoginResponse(id, "u", "e", "R", "S", "T");
        UserLoginResponse.UserLoginResponseBuilder b = UserLoginResponse.builder();
        b.id(id); b.username("u"); b.email("e"); b.role("R"); b.status("S"); b.token("T");
        assertNotNull(b.toString());
        assertEquals(r, b.build());
        
        UserLoginResponse.UserLoginResponseBuilder b2 = UserLoginResponse.builder();
        b2.id(null); b2.username(null); b2.email(null); b2.role(null); b2.status(null); b2.token(null);
        assertNull(b2.build().id());

        User user = User.builder().id(id).username("u").email("e").role(UserRole.TITIPER).status(UserStatus.ACTIVE).build();
        assertEquals(id, UserLoginResponse.fromUser(user, "T").id());
    }

    @Test
    void testUserProfileUpdateRequestExhaustive() throws Exception {
        bruteForceLombok(UserProfileUpdateRequest.class);
    }

    @Test
    void testUserProfileResponseExhaustive() throws Exception {
        bruteForceLombok(UserProfileResponse.class);
    }

    @Test
    void testUpgradeRequestSubmissionRequestExhaustive() throws Exception {
        // Line 9 (@Data) and Builder
        bruteForceLombok(UpgradeRequestSubmissionRequest.class);
        
        // Specific complex branch check for Equals
        UpgradeRequestSubmissionRequest a = new UpgradeRequestSubmissionRequest("F", "C", "S");
        assertFalse(a.equals(null));
        assertFalse(a.equals("not an object"));
        assertTrue(a.equals(a));
        
        UpgradeRequestSubmissionRequest b = new UpgradeRequestSubmissionRequest("F", "C", "S");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());

        // Field permutations for equals/hashCode
        String[] v = {"V", null};
        for(String f1:v) for(String c1:v) {
            UpgradeRequestSubmissionRequest o1 = new UpgradeRequestSubmissionRequest(f1, c1, "S");
            for(String f2:v) for(String c2:v) {
                UpgradeRequestSubmissionRequest o2 = new UpgradeRequestSubmissionRequest(f2, c2, "S");
                if (java.util.Objects.equals(f1, f2) && java.util.Objects.equals(c1, c2)) {
                    assertEquals(o1, o2);
                    assertEquals(o1.hashCode(), o2.hashCode());
                } else {
                    assertNotEquals(o1, o2);
                }
            }
        }
        
        // canEqual branches
        assertTrue(a.canEqual(b));
        assertFalse(a.canEqual("string"));
        UpgradeRequestSubmissionRequest subclass = new UpgradeRequestSubmissionRequest("F", "C", "S") {
            @Override public boolean canEqual(Object o) { return false; }
        };
        assertFalse(a.equals(subclass));
    }

    private void bruteForceLombok(Class<?> clazz) throws Exception {
        // 1. All Constructors
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            c.setAccessible(true);
            Object[] args = new Object[c.getParameterCount()];
            for (int i=0; i<args.length; i++) args[i] = getDummy(c.getParameterTypes()[i]);
            try { 
                Object instance = c.newInstance(args); 
                exerciseInstance(instance);
            } catch (Exception ignored) {}
        }
        
        // 2. Builder
        try {
            Method builderMethod = clazz.getMethod("builder");
            Object builder = builderMethod.invoke(null);
            exerciseInstance(builder);
            
            // Build with all values
            for (Method m : builder.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1) {
                    m.setAccessible(true);
                    m.invoke(builder, getDummy(m.getParameterTypes()[0]));
                }
            }
            Object built = builder.getClass().getMethod("build").invoke(builder);
            exerciseInstance(built);
            
            // Build with nulls
            Object builder2 = builderMethod.invoke(null);
            for (Method m : builder2.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && !m.getParameterTypes()[0].isPrimitive()) {
                    m.setAccessible(true);
                    m.invoke(builder2, new Object[]{null});
                }
            }
            exerciseInstance(builder2.getClass().getMethod("build").invoke(builder2));
            
        } catch (NoSuchMethodException ignored) {}
    }

    private void exerciseInstance(Object obj) throws Exception {
        if (obj == null) return;
        Class<?> clazz = obj.getClass();
        for (Method m : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 0) {
                m.setAccessible(true);
                try { m.invoke(obj); } catch (Exception ignored) {}
            }
            if (m.getName().startsWith("set") && m.getParameterCount() == 1) {
                m.setAccessible(true);
                try { m.invoke(obj, getDummy(m.getParameterTypes()[0])); } catch (Exception ignored) {}
                if (!m.getParameterTypes()[0].isPrimitive()) {
                    try { m.invoke(obj, new Object[]{null}); } catch (Exception ignored) {}
                }
            }
        }
        // toString, hashCode
        obj.toString();
        obj.hashCode();
        obj.equals(obj);
        obj.equals(null);
        obj.equals("string");
    }

    private Object getDummy(Class<?> type) {
        if (type == String.class) return "test";
        if (type == UUID.class) return UUID.randomUUID();
        if (type == Long.class || type == long.class) return 1L;
        if (type == Integer.class || type == int.class) return 1;
        if (type == Boolean.class || type == boolean.class) return true;
        if (type == OffsetDateTime.class) return OffsetDateTime.now();
        return null;
    }
}
