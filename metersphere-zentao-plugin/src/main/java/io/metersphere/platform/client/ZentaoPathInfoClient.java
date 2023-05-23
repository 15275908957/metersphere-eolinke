package io.metersphere.platform.client;


import io.metersphere.platform.domain.RequestUrl;

import java.util.regex.Pattern;

public class ZentaoPathInfoClient extends ZentaoClient {

    private static final String LOGIN = "/user-login.json?zentaosid=";
    private static final String SESSION_GET = "/api-getsessionid.json";
    private static final String BUG_CREATE = "/api-getModel-bug-create.json?zentaosid=";
    private static final String BUG_UPDATE = "/api-getModel-bug-update-bugID={1}.json?zentaosid={2}";
    private static final String BUG_DELETE = "/bug-delete-{1}-yes.json?zentaosid={2}";
    private static final String BUG_GET = "/api-getModel-bug-getById-bugID={1}.json?zentaosid={2}";
    private static final String STORY_GET = "/api-getModel-story-getProductStories-productID={key}.json?zentaosid=";
    private static final String USER_GET = "/api-getModel-user-getList.json?zentaosid=";
    private static final String BUILDS_GET = "/api-getModel-build-getProductBuildPairs-productID={0}.json?zentaosid={1}";
    private static final String BUILDS_GET_V17 = "/api-getModel-build-getBuildPairs-productID={0}.json?zentaosid={1}";
    private static final String CREATE_META_DATA="/bug-create-{0}.json?zentaosid={1}";
    private static final String FILE_UPLOAD = "/api-getModel-file-saveUpload-objectType=bug,objectID={1}.json?zentaosid={2}";
    private static final String FILE_DELETE = "/file-delete-{1}-.yes.json?zentaosid={2}";
    private static final String FILE_DOWNLOAD="/file-download-{1}-.click.json?zentaosid={2}";
    private static final String REPLACE_IMG_URL = "<img src=\"%s/file-read-$1\"/>";
    private static final Pattern IMG_PATTERN = Pattern.compile("file-read-(.*?)\"/>");
    private static final String PRODUCT_GET = "/product-view-{0}.json?zentaosid={1}";
    private static final String BUG_LIST_URL = "/bug-browse-{1}-0-all-0--{2}-{3}-{4}.json?&zentaosid={5}";
    private static final String GET_TESTCASE = "/api-getModel-testcase-getByStatus-productID={1}.json?zentaosid=";
    //browse($productID = 0, $branch = 'all', $browseType = 'all', $param = 0, $orderBy = 'id_desc', $recTotal = 0, $recPerPage = 20, $pageID = 1, $projectID = 0
//    private static final String GET_TESTCASE = "/testcase-browse-{1}-all-all-0-id_desc-0-0-0-{2}.json&zentaosid=";
    private static final String GET_MODULE_PAIRS = "/api-getModel-tree-getAllModulePairs-type={1}.json?zentaosid=";
    private static final String GET_PROJECT_TREE = "/tree-browse-{1}-case-0-0-project.json?zentaosid=";
    private static final String AJAX_GET_PRODUCT_STORIES = "/story-ajaxGetProductStories-{1}-0-{2}-0-false-noclosed-0-full-1-0.json?zentaosid=";
    private static final String GET_TREE_MENU = "/api-getModel-tree-getCaseTreeMenu-rootID={1}-productID={2}.json?zentaosid=";
    private static final String CREATE_MODULE = "/api-getModel-tree-manageChild-rootID={1},type={2}.json?zentaosid=";
//    private static final String CREATE_TESTCASE = "/api-getModel-testcase-create-productID={1}.json?zentaosid=";
    private static final String CREATE_TESTCASE = "/testcase-create-{1}-case-{2}-project.json?zentaosid=";
    private static final String TEST_CASE_VIEW = "/testcase-view-{1}-1.json?zentaosid=";

    private static final String LINKED_2_PROJECT = "/api-getModel-action-create-objectType={1},objectID={2},actionType={3},comment={4},extra={5}.json?zentaosid=";
    private static final String UPDATE_TESTCASE = "/testcase-edit-{1}.json?zentaosid=";
    private static final String SET_MENU = "/api-getModel-project-setMenu-objectID={1}.json?zentaosid=";
    private static final String GET_RELATED_FIELDS = "/api-getModel-action-getRelatedFields-objectType={1},objectID={2},actionType={3},extra={4}.json?zentaosid=";
    private static final String EXECUTION_TESTCASE = "/execution-testcase-{1}.json?zentaosid=";
    private static final String SAVE_STATE = "/api-getModel-execution-saveState-executionID={1},executions={2}.json?zentaosid=";
    private static final String GET_BY_ID = "/api-getModel-execution-getByID-executionID={1}.json?zentaosid=";
    private static final String SET_PROJECT_SESSION = "/api-getModel-execution-setProjectSession-executionID={1}.json?zentaosid=";
    private static final String TREE_BROWSE = "/tree-browse-{1}-case-0-0-qa.json?zentaosid=";



    public ZentaoPathInfoClient(String url) {
        super(url);
    }

    protected RequestUrl request = new RequestUrl();

    {
        request.setLogin(getUrl(LOGIN));
        request.setSessionGet(getUrl(SESSION_GET));
        request.setBugCreate(getUrl(BUG_CREATE));
        request.setBugGet(getUrl(BUG_GET));
        request.setStoryGet(getUrl(STORY_GET));
        request.setUserGet(getUrl(USER_GET));
        request.setBuildsGet(getUrl(BUILDS_GET));
        request.setBuildsGetV17(getUrl(BUILDS_GET_V17));
        request.setFileUpload(getUrl(FILE_UPLOAD));
        request.setReplaceImgUrl(getReplaceImgUrl(REPLACE_IMG_URL));
        request.setImgPattern(IMG_PATTERN);
        request.setBugUpdate(getUrl(BUG_UPDATE));
        request.setBugDelete(getUrl(BUG_DELETE));
        request.setBugList(getUrl(BUG_LIST_URL));
        request.setCreateMetaData(getUrl(CREATE_META_DATA));
        request.setProductGet(getUrl(PRODUCT_GET));
        request.setFileDelete(getUrl(FILE_DELETE));
        request.setFileDownload(getUrl(FILE_DOWNLOAD));
        request.setGetTreeMeun(getUrl(GET_TREE_MENU));
        request.setGetTestCase(getUrl(GET_TESTCASE));
        request.setGetModuleListByType(getUrl(GET_MODULE_PAIRS));
        request.setCreateModule(getUrl(CREATE_MODULE));
        request.setCreateTestcase(getUrl(CREATE_TESTCASE));
        request.setUpdateTestcase(getUrl(UPDATE_TESTCASE));
        request.setLinked2project(getUrl(LINKED_2_PROJECT));
        request.setSetMenu(getUrl(SET_MENU));
        request.setRelatedFields(getUrl(GET_RELATED_FIELDS));
        request.setExecutionTestcase(getUrl(EXECUTION_TESTCASE));
        request.setSaveState(getUrl(SAVE_STATE));
        request.setGetByID(getUrl(GET_BY_ID));
        request.setSetProjectSession(getUrl(SET_PROJECT_SESSION));
        request.setTreeBrowse(getUrl(TREE_BROWSE));
        request.setGetTreeProject(getUrl(GET_PROJECT_TREE));
        request.setAjaxGetProductStories(getUrl(AJAX_GET_PRODUCT_STORIES));
        request.setTestcaseView(getUrl(TEST_CASE_VIEW));
        requestUrl = request;
    }

    protected String getUrl(String url) {
        return getBaseUrl() + url;
    }
}
