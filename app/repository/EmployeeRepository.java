package repository;

import io.ebean.*;
import models.Employee;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class EmployeeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EmployeeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Return a paged list of Employee
     *
     * @param page     Page to display
     * @param pageSize Number of Employees per page
     * @param sortBy   Employee property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public CompletionStage<PagedList<Employee>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() ->
                ebeanServer.find(Employee.class).where()
                    .ilike("name", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList(), executionContext);
    }

//    public CompletionStage<Optional<Employee>> lookup(Long id) {
//        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Employee.class).setId(id).findOne()), executionContext);
//    }
//
//    public CompletionStage<Optional<Long>> update(Long id, Employee newEmployeeData) {
//        return supplyAsync(() -> {
//            Transaction txn = ebeanServer.beginTransaction();
//            Optional<Long> value = Optional.empty();
//            try {
//                Employee savedEmployee = ebeanServer.find(Employee.class).setId(id).findOne();
//                if (savedEmployee != null) {
//                    savedEmployee.company = newEmployeeData.company;
//                    savedEmployee.discontinued = newEmployeeData.discontinued;
//                    savedEmployee.introduced = newEmployeeData.introduced;
//                    savedEmployee.name = newEmployeeData.name;
//
//                    savedEmployee.update();
//                    txn.commit();
//                    value = Optional.of(id);
//                }
//            } finally {
//                txn.end();
//            }
//            return value;
//        }, executionContext);
//    }
//
//    public CompletionStage<Optional<Long>>  delete(Long id) {
//        return supplyAsync(() -> {
//            try {
//                final Optional<Employee> EmployeeOptional = Optional.ofNullable(ebeanServer.find(Employee.class).setId(id).findOne());
//                EmployeeOptional.ifPresent(Model::delete);
//                return EmployeeOptional.map(c -> c.id);
//            } catch (Exception e) {
//                return Optional.empty();
//            }
//        }, executionContext);
//    }
//
//    public CompletionStage<Long> insert(Employee Employee) {
//        return supplyAsync(() -> {
//             Employee.id = System.currentTimeMillis(); // not ideal, but it works
//             ebeanServer.insert(Employee);
//             return Employee.id;
//        }, executionContext);
//    }
}
