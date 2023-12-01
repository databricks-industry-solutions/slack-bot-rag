package com.databricks.gtm;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfiguration extends CamelCaseToUnderscoresNamingStrategy {

    @Value("${databricks.audit.table}")
    private String tableName;

    @Value("${databricks.audit.schema}")
    private String schemaName;

    @Override
    public Identifier toPhysicalTableName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return Identifier.toIdentifier(tableName);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return Identifier.toIdentifier(schemaName);
    }

}