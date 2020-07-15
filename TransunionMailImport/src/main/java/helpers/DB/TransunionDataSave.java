package helpers.DB;

import Entity.TransunionEntity;
import Entity.TransunionEntityHelper;
import com.amazonaws.services.s3.internal.S3QueryStringSigner;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransunionDataSave {
    private static final String TRUNCATE_STATEMENT = "truncate table TRANSUNIONCODES_WRITE;";
    private static final String upsertStatement =
            "\ninsert into TRANSUNIONCODES_WRITE(mmcode,vehicletype,make,model,variant,regyear,publicationsection,mastermodel,makecode,modelcode,variantcode," +
                    "axleconfiguration,bodytype,noofdoors,drive,seats,`use`,wheelbase,manualauto,nogears,cooling,cubiccapacity,cylconfiguration," +
                    "enginecycle,fueltanksize,fueltype,kilowatts,nocylinders,turboorsupercharged,gcm,gvm,tare,origin,frontnotyres,fronttyresize," +
                    "rearnotyres,reartyresize,introdate,discdate,co2,`length`,height,width,newlistprice) " +
            "values " +
                    "(:mmcode,:vehicletype,:make,:model,:variant,:regyear,:publicationsection,:mastermodel,:makecode,:modelcode,:variantcode," +
                    ":axleconfiguration,:bodytype,:noofdoors,:drive,:seats,:use,:wheelbase,:manualauto,:nogears,:cooling,:cubiccapacity," +
                    ":cylconfiguration,:enginecycle,:fueltanksize,:fueltype,:kilowatts,:nocylinders,:turboorsupercharged,:gcm,:gvm,:tare,:origin," +
                    ":frontnotyres,:fronttyresize,:rearnotyres,:reartyresize,:introdate,:discdate,:co2,:length,:height,:width,:newlistprice) ";
     private static final String CLEANUP =
             "insert into TRANSUNIONCODES\n" +
                     "(mmCode, vehicleType, make, model, variant, regYear, publicationSection, masterModel, makeCode, modelCode, variantCode, axleConfiguration, bodyType, noOfDoors, drive, seats, `use`, wheelbase, manualAuto, noGears, cooling, cubicCapacity, cylConfiguration, engineCycle, fuelTankSize, fuelType, kilowatts, noCylinders, turboOrSuperCharged, gcm, gvm, tare, origin, frontNoTyres, frontTyreSize, rearNoTyres, rearTyreSize, introDate, discDate, co2, length, height, width, newListPrice)\n" +
                     "Select writeTable.* from TRANSUNIONCODES_WRITE writeTable\n" +
                     "    left join TRANSUNIONCODES readTable on readTable.mmCode = writeTable.mmCode\n" +
                     "        and readTable.regYear = writeTable.regYear\n" +
                     "where readTable.mmCode is null;\n" +
                     "SET SQL_SAFE_UPDATES=0;\n" +
                     "update TRANSUNIONCODES readTable\n" +
                     "inner join TRANSUNIONCODES_WRITE writeTable\n" +
                     "on readTable.mmCode = writeTable.mmCode\n" +
                     "    and readTable.regYear = writeTable.regYear\n" +
                     "set\n" +
                     "    readTable.mmCode = writeTable.mmCode,\n" +
                     "    readTable.vehicleType = writeTable.vehicleType,\n" +
                     "    readTable.make = writeTable.make,\n" +
                     "    readTable.model = writeTable.model,\n" +
                     "    readTable.variant = writeTable.variant,\n" +
                     "    readTable.regYear = writeTable.regYear,\n" +
                     "    readTable.publicationSection = writeTable.publicationSection,\n" +
                     "    readTable.masterModel = writeTable.masterModel,\n" +
                     "    readTable.makeCode = writeTable.makeCode,\n" +
                     "    readTable.modelCode = writeTable.modelCode,\n" +
                     "    readTable.variantCode = writeTable.variantCode,\n" +
                     "    readTable.axleConfiguration = writeTable.axleConfiguration,\n" +
                     "    readTable.bodyType = writeTable.bodyType,\n" +
                     "    readTable.noOfDoors = writeTable.noOfDoors,\n" +
                     "    readTable.drive = writeTable.drive,\n" +
                     "    readTable.seats = writeTable.seats,\n" +
                     "    readTable.use = writeTable.use,\n" +
                     "    readTable.wheelbase = writeTable.wheelbase,\n" +
                     "    readTable.manualAuto = writeTable.manualAuto,\n" +
                     "    readTable.noGears = writeTable.noGears,\n" +
                     "    readTable.cooling = writeTable.cooling,\n" +
                     "    readTable.cubicCapacity = writeTable.cubicCapacity,\n" +
                     "    readTable.cylConfiguration = writeTable.cylConfiguration,\n" +
                     "    readTable.engineCycle = writeTable.engineCycle,\n" +
                     "    readTable.fuelTankSize = writeTable.fuelTankSize,\n" +
                     "    readTable.fuelType = writeTable.fuelType,\n" +
                     "    readTable.kilowatts = writeTable.kilowatts,\n" +
                     "    readTable.noCylinders = writeTable.noCylinders,\n" +
                     "    readTable.turboOrSuperCharged = writeTable.turboOrSuperCharged,\n" +
                     "    readTable.gcm = writeTable.gcm,\n" +
                     "    readTable.gvm = writeTable.gvm,\n" +
                     "    readTable.tare = writeTable.tare,\n" +
                     "    readTable.origin = writeTable.origin,\n" +
                     "    readTable.frontNoTyres = writeTable.frontNoTyres,\n" +
                     "    readTable.frontTyreSize = writeTable.frontTyreSize,\n" +
                     "    readTable.rearNoTyres = writeTable.rearNoTyres,\n" +
                     "    readTable.rearTyreSize = writeTable.rearTyreSize,\n" +
                     "    readTable.introDate = writeTable.introDate,\n" +
                     "    readTable.discDate = writeTable.discDate,\n" +
                     "    readTable.co2 = writeTable.co2,\n" +
                     "    readTable.length = writeTable.length,\n" +
                     "    readTable.height = writeTable.height,\n" +
                     "    readTable.width = writeTable.width,\n" +
                     "    readTable.newListPrice = writeTable.newListPrice;\n" +
                     "truncate table TRANSUNIONCODES_WRITE;\n" +
                     "SET SQL_SAFE_UPDATES=1;\n";

    public static void saveData(List<TransunionEntity> entityList) throws Exception{
        NamedParameterJdbcTemplate jdbcTemplate = ConnectionManager.getNamedJdbcTemplate();
        List<MapSqlParameterSource> parameterList = new ArrayList();
        entityList.stream().forEach(entity -> {
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            TransunionEntityHelper.myFields.forEach((key,field) -> {
                try {
                    paramSource.addValue(key, TransunionEntityHelper.getMethods.get(key).invoke(entity));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println(e.getMessage());
                }
            });
            parameterList.add(paramSource);
        });
        MapSqlParameterSource[] array = new MapSqlParameterSource[parameterList.size()];
        array = parameterList.toArray(array);
        jdbcTemplate.batchUpdate(upsertStatement, array);
    }

    public static void truncateTransunionWrite() throws Exception{
        try(Statement stmt = ConnectionManager.getConnection().createStatement();){
            stmt.execute(TRUNCATE_STATEMENT);
        }
    }

    public static void updateReadTable() throws Exception{
        try(Statement stmt = ConnectionManager.getConnection().createStatement();) {
            stmt.execute(CLEANUP);
        }
    }
}
