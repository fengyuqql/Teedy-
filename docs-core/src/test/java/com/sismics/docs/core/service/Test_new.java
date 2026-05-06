package com.sismics.docs.core.service;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.FileDao;
import com.sismics.docs.core.dao.dto.*;
import com.sismics.docs.core.model.jpa.*;
import com.sismics.docs.core.util.DirectoryUtil;
import com.sismics.docs.core.util.FileUtil;
import com.sismics.util.EnvironmentUtil;
import com.sismics.util.LocaleUtil;
import com.sismics.util.MessageUtil;
import com.sismics.util.ResourceUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Test_new extends BaseTransactionalTest {

    @Test
    public void processFileMissingStoredFileTest() throws Exception {
        User user = createUser("processFileMissingStoredFileTest");

        FileDao fileDao = new FileDao();
        File file = createFile(user, File.UNKNOWN_SIZE);

        Path storedFile = DirectoryUtil.getStorageDirectory().resolve(file.getId());
        Assert.assertTrue(Files.exists(storedFile));
        Files.delete(storedFile);
        Assert.assertFalse(Files.exists(storedFile));

        FileSizeService fileSizeService = new FileSizeService();
        fileSizeService.processFile(file);

        Assert.assertEquals(File.UNKNOWN_SIZE, fileDao.getFile(file.getId()).getSize());
    }

    @Test
    public void processFileMissingUserTest() {
        File file = new File();
        file.setId("dummy_file_id");
        file.setUserId("missing_user_id");
        file.setSize(File.UNKNOWN_SIZE);

        FileSizeService fileSizeService = new FileSizeService();
        fileSizeService.processFile(file);

        Assert.assertEquals(File.UNKNOWN_SIZE, file.getSize());
    }

    @Test
    public void fileUtilDeleteAndProcessingTest() throws Exception {
        String fileId = "delete_test_" + UUID.randomUUID();
        Path storageDir = DirectoryUtil.getStorageDirectory();
        Path storedFile = storageDir.resolve(fileId);
        Path webFile = storageDir.resolve(fileId + "_web");
        Path thumbnailFile = storageDir.resolve(fileId + "_thumb");

        Files.write(storedFile, "a".getBytes(StandardCharsets.UTF_8));
        Files.write(webFile, "b".getBytes(StandardCharsets.UTF_8));
        Files.write(thumbnailFile, "c".getBytes(StandardCharsets.UTF_8));

        Assert.assertTrue(Files.exists(storedFile));
        Assert.assertTrue(Files.exists(webFile));
        Assert.assertTrue(Files.exists(thumbnailFile));

        FileUtil.delete(fileId);

        Assert.assertFalse(Files.exists(storedFile));
        Assert.assertFalse(Files.exists(webFile));
        Assert.assertFalse(Files.exists(thumbnailFile));

        // Exercise the non-existent branches.
        FileUtil.delete(fileId);

        Assert.assertFalse(FileUtil.isProcessingFile(fileId));
        FileUtil.startProcessingFile(fileId);
        Assert.assertTrue(FileUtil.isProcessingFile(fileId));
        FileUtil.endProcessingFile(fileId);
        Assert.assertFalse(FileUtil.isProcessingFile(fileId));
    }

    @Test
    public void getFileSizeDecryptFailureTest() throws Exception {
        User user = createUser("getFileSizeDecryptFailureTest");
        File file = createFile(user, File.UNKNOWN_SIZE);

        user.setPrivateKey(null);
        long fileSize = FileUtil.getFileSize(file.getId(), user);

        Assert.assertEquals(File.UNKNOWN_SIZE.longValue(), fileSize);
    }

    @Test
    public void localeUtilParsingTest() {
        Assert.assertEquals(Locale.ENGLISH, LocaleUtil.getLocale(null));
        Assert.assertEquals(Locale.ENGLISH, LocaleUtil.getLocale(""));

        Locale zh = LocaleUtil.getLocale("zh_CN");
        Assert.assertEquals("zh", zh.getLanguage());
        Assert.assertEquals("CN", zh.getCountry());
        Assert.assertEquals("", zh.getVariant());

        Locale posix = LocaleUtil.getLocale("en_US_POSIX");
        Assert.assertEquals("en", posix.getLanguage());
        Assert.assertEquals("US", posix.getCountry());
        Assert.assertEquals("POSIX", posix.getVariant());
    }

    @Test
    public void environmentUtilFlagsTest() {
        boolean originalWebappContext = EnvironmentUtil.isWebappContext();
        try {
            EnvironmentUtil.setWebappContext(false);
            Assert.assertTrue(EnvironmentUtil.isUnitTest());
            EnvironmentUtil.setWebappContext(true);
            Assert.assertFalse(EnvironmentUtil.isUnitTest());
        } finally {
            EnvironmentUtil.setWebappContext(originalWebappContext);
        }

        Assert.assertTrue(EnvironmentUtil.isWindows() || EnvironmentUtil.isMacOs() || EnvironmentUtil.isUnix());
        Assert.assertNotNull(EnvironmentUtil.getMacOsUserHome());
    }

    @Test
    public void messageUtilLookupTest() {
        String known = MessageUtil.getMessage(Locale.ENGLISH, "email.no_html.error");
        Assert.assertNotNull(known);
        Assert.assertNotEquals("**email.no_html.error**", known);

        String missing = MessageUtil.getMessage(Locale.ENGLISH, "missing.key");
        Assert.assertEquals("**missing.key**", missing);

        Assert.assertNotNull(MessageUtil.getMessage(Locale.ENGLISH));
    }

    @Test
    public void resourceUtilListTest() throws Exception {
        List<String> files = ResourceUtil.list(Test_new.class, "/file");
        Assert.assertTrue(files.contains(FILE_JPG));

        List<String> pdfs = ResourceUtil.list(Test_new.class, "/file", (dir, name) -> name.endsWith(".pdf"));
        Assert.assertTrue(pdfs.contains(FILE_PDF));
        Assert.assertFalse(pdfs.contains(FILE_JPG));
    }

    @Test
    public void dtoAndModelCoverageTest() throws Exception {
        Object[] beans = new Object[] {
                new AclDto(),
                new AuditLogDto(),
                new CommentDto(),
                new ContributorDto(),
                new DocumentDto(),
                new DocumentMetadataDto(),
                new GroupDto(),
                new MetadataDto(),
                new RelationDto(),
                new RouteDto(),
                new RouteModelDto(),
                new RouteStepDto(),
                new TagDto(),
                new UserDto(),
                new WebhookDto(),
                new Acl(),
                new AuditLog(),
                new AuthenticationToken(),
                new BaseFunction(),
                new Comment(),
                new Config(),
                new Contributor(),
                new Document(),
                new DocumentMetadata(),
                new DocumentTag(),
                new File(),
                new Group(),
                new Metadata(),
                new PasswordRecovery(),
                new Relation(),
                new Role(),
                new RoleBaseFunction(),
                new Route(),
                new RouteModel(),
                new RouteStep(),
                new Share(),
                new Tag(),
                new User(),
                new UserGroup(),
                new Vocabulary(),
                new Webhook()
        };

        for (Object bean : beans) {
            exerciseBean(bean);
        }
    }

    private static void exerciseBean(Object bean) throws Exception {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (isSetter(method)) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object value = sampleValue(paramType);
                if (value != null || !paramType.isPrimitive()) {
                    method.invoke(bean, value);
                }
            }
        }

        for (Method method : methods) {
            if (isGetter(method)) {
                method.invoke(bean);
            }
        }

        bean.toString();
        if (bean instanceof Loggable) {
            ((Loggable) bean).toMessage();
        }
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterCount() == 1
                && method.getDeclaringClass() != Object.class;
    }

    private static boolean isGetter(Method method) {
        if (method.getParameterCount() != 0 || method.getDeclaringClass() == Object.class) {
            return false;
        }
        if (method.getName().equals("getClass")) {
            return false;
        }
        if (method.getName().startsWith("get") && method.getReturnType() != void.class) {
            return true;
        }
        return method.getName().startsWith("is")
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class);
    }

    private static Object sampleValue(Class<?> type) throws Exception {
        if (type == String.class) {
            return "value";
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 2L;
        }
        if (type == boolean.class || type == Boolean.class) {
            return true;
        }
        if (type == double.class || type == Double.class) {
            return 3.14d;
        }
        if (type == float.class || type == Float.class) {
            return 2.71f;
        }
        if (type == short.class || type == Short.class) {
            return (short) 7;
        }
        if (type == byte.class || type == Byte.class) {
            return (byte) 8;
        }
        if (type == char.class || type == Character.class) {
            return 'x';
        }
        if (type == Date.class) {
            return new Date(0L);
        }
        if (type == UUID.class) {
            return UUID.randomUUID();
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants.length > 0 ? constants[0] : null;
        }
        if (type.isArray()) {
            Class<?> component = type.getComponentType();
            Object array = Array.newInstance(component, 1);
            Object element = sampleValue(component);
            if (element != null || !component.isPrimitive()) {
                Array.set(array, 0, element);
            }
            return array;
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        if (Set.class.isAssignableFrom(type)) {
            return new HashSet<>();
        }
        if (Map.class.isAssignableFrom(type)) {
            return new HashMap<>();
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
