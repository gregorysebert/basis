package basis.migration.service;

/**
 * Created by IntelliJ IDEA.
 * User: Janssens-b
 * Date: 05-oct.-2010
 * Time: 14:25:56
 */
public interface MigrationService {

    public void MigrateAll(String folderPath) throws Exception;

    public void MigrateDocument(String docPath) throws Exception;

}
