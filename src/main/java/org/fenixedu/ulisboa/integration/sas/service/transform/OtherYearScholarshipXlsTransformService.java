package org.fenixedu.ulisboa.integration.sas.service.transform;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentFirstYearBean;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentOtherYearBean;
import org.fenixedu.ulisboa.integration.sas.util.SASDomainException;

public class OtherYearScholarshipXlsTransformService extends AbstractScholarshipXlsTransformService {

    static Integer FIRST_VALUE_ROW = 2;
    static Integer TEST_VALUE_ROW = null; //3 - 1;

    @Override
    public boolean checkExcelFormat(HSSFSheet sheet) throws IOException {
        final int columnsRead = Integer.valueOf(String.valueOf(sheet.getRow(0).getLastCellNum()));
        final int columnsExpected = ScholarshipStudentOtherYearBean.INGRESSION_REGIME + 1;
        final int columnsAlternative = ScholarshipStudentFirstYearBean.INGRESSION_REGIME + 1;

        if (columnsRead == columnsExpected) {
            return true;
        }

        if (columnsRead == columnsAlternative) {
            throw new SASDomainException("error.fileTypeDoesNotMatchRequest.expected" + getClass().getSimpleName());
        }

        throw new SASDomainException("error.fileFormatDoesNotMatchRequest.expected",
                new String[] { String.valueOf(columnsExpected), String.valueOf(columnsRead) });
    }

    @Override
    public void readStudentLines(HSSFSheet sheet) {
        int i = FIRST_VALUE_ROW;
        HSSFRow row;
        while ((row = sheet.getRow(i)) != null && row.getCell(0) != null) {
            if (TEST_VALUE_ROW != null && i != TEST_VALUE_ROW.intValue()) {
                i++;
                continue;
            }

            ScholarshipStudentOtherYearBean bean = new ScholarshipStudentOtherYearBean();
            readSpreadsheetRow(row, bean);
            studentLines.add(bean);
            i++;
        }
    }

    private void readSpreadsheetRow(HSSFRow row, ScholarshipStudentOtherYearBean bean) {
        bean.setInstitutionCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.INSTITUTION_CODE));
        bean.setInstitutionName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.INSTITUTION_NAME));
        bean.setCandidacyNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CANDIDACY_NUMBER));
        try {
            bean.setStudentNumber(
                    Integer.parseInt(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.STUDENT_NUMBER)));
        } catch (NumberFormatException e) {
            bean.setStudentNumber(null);
        }
        bean.setStudentName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.STUDENT_NAME));
        bean.setDocumentTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DOCUMENT_TYPE_NAME));
        bean.setDocumentNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DOCUMENT_NUMBER));
        bean.setDegreeCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_CODE));
        bean.setDegreeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_NAME));
        bean.setDegreeTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_TYPE_NAME));
        bean.setCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CODE));
        try {
            bean.setCycleIngressionYear(
                    Integer.parseInt(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CYCLE_INGRESSION_YEAR)));
        } catch (NumberFormatException e) {
            bean.setCycleIngressionYear(null);
        }
        bean.setDocumentBINumber(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DOCUMENT_BI));
    }

    @Override
    public void writeExcelLines(HSSFSheet sheet) throws IOException {
        for (int i = 0; i < studentLines.size(); i++) {
            ScholarshipStudentOtherYearBean scholarshipStudentBean = (ScholarshipStudentOtherYearBean) studentLines.get(i);
            HSSFRow row = sheet.getRow(FIRST_VALUE_ROW + i);
            writeSpreadsheetRow(row, scholarshipStudentBean);
        }
    }

    private void writeSpreadsheetRow(HSSFRow row, ScholarshipStudentOtherYearBean bean) {
        writeCellString(row, ScholarshipStudentOtherYearBean.INSTITUTION_CODE, bean.getInstitutionCode());
        writeCellInteger(row, ScholarshipStudentOtherYearBean.STUDENT_NUMBER, bean.getStudentNumber());
        writeCellString(row, ScholarshipStudentOtherYearBean.DEGREE_CODE, bean.getDegreeCode());
        writeCellInteger(row, ScholarshipStudentOtherYearBean.COUNT_NUMBER_OF_DEGREE_CHANGES, bean.getNumberOfDegreeChanges());
        writeCellString(row, ScholarshipStudentOtherYearBean.CURRENT_YEAR_HAS_MADE_DEGREE_CHANGE,
                booleanToString(bean.getHasMadeDegreeChangeOnCurrentYear()));
        writeCellString(row, ScholarshipStudentOtherYearBean.REGISTERED, booleanToString(bean.getRegistered()));
        writeCellLocalDate(row, ScholarshipStudentOtherYearBean.REGISTRATION_DATE,
                bean.getRegistered() ? bean.getRegistrationDate() : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.REGIME, bean.getRegistered() ? bean.getRegime() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.CYCLE_INGRESSION_YEAR,
                bean.getRegistered() ? bean.getCycleIngressionYear() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.CYCLE_NUMBER_OF_ENROLMENT_YEARS,
                bean.getRegistered() ? bean.getCycleNumberOfEnrolmentYears() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.CYCLE_COUNT_NUMBER_OF_ENROLMENTS_YEARS_IN_INTEGRAL_REGIME,
                bean.getRegistered() ? bean.getCycleNumberOfEnrolmentsYearsInIntegralRegime() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.NUMBER_OF_APPROVED_ECTS,
                bean.getRegistered() ? bean.getNumberOfApprovedEcts() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.NUMBER_OF_YEARS_DEGREE,
                bean.getRegistered() ? bean.getNumberOfDegreeCurricularYears() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.LAST_ENROLMENT_CURRICULAR_YEAR,
                bean.getRegistered() ? bean.getLastEnrolmentCurricularYear() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.NUMBER_OF_ENROLLED_ECTS_LAST_YEAR,
                bean.getRegistered() ? bean.getNumberOfEnrolledEctsLastYear() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.NUMBER_OF_APPROVED_ECTS_LAST_YEAR,
                bean.getRegistered() ? bean.getNumberOfApprovedEctsLastYear() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.CURRICULAR_YEAR,
                bean.getRegistered() ? bean.getCurricularYear() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.NUMBER_OF_ECTS,
                bean.getRegistered() ? bean.getNumberOfEnrolledECTS() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.GRATUITY,
                bean.getRegistered() ? bean.getGratuityAmount() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.NUMBER_OF_MONTHS_EXECUTION_YEAR,
                bean.getRegistered() ? bean.getNumberOfMonthsExecutionYear() : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.FIRST_MONTH_EXECUTION_YEAR,
                bean.getRegistered() ? bean.getFirstMonthExecutionYear() : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OWNER_CET,
                bean.getRegistered() ? booleanToString(bean.getCetQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OWNER_CSTP,
                bean.getRegistered() ? booleanToString(bean.getCtspQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OWNER_BACHELOR,
                bean.getRegistered() ? booleanToString(bean.getDegreeQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OWNER_MASTER,
                bean.getRegistered() ? booleanToString(bean.getMasterQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OWNER_PHD,
                bean.getRegistered() ? booleanToString(bean.getPhdQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.OBSERVATIONS, bean.getObservations());
        writeCellInteger(row, ScholarshipStudentOtherYearBean.LAST_ENROLMENT_EXECUTION_YEAR,
                bean.getRegistered() ? bean.getLastEnrolmentYear() : null);
        writeCellLocalDate(row, ScholarshipStudentOtherYearBean.LAST_ACADEMIC_ACT_DATE_LAST_YEAR,
                bean.getRegistered() ? bean.getLastAcademicActDateLastYear() : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.INGRESSION_REGIME,
                bean.getIngressionRegimeCodeWithDescription());
    }

    public OtherYearScholarshipXlsTransformService(POIFSFileSystem poifsFileSystem) {
        scholarshipStudentBean = new ScholarshipStudentOtherYearBean();
        this.studentLines = new ArrayList<>();
        this.poifsFileSystem = poifsFileSystem;
    }

}
