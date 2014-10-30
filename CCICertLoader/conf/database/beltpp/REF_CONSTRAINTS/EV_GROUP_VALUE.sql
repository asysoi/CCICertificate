--------------------------------------------------------
--  Ref Constraints for Table EV_GROUP_VALUE
--------------------------------------------------------

  ALTER TABLE "EV_GROUP_VALUE" ADD CONSTRAINT "GROUP_GROUP_FK" FOREIGN KEY ("ID_GROUP")
	  REFERENCES "EV_GROUP" ("ID") ENABLE;
  ALTER TABLE "EV_GROUP_VALUE" ADD CONSTRAINT "GROUP_MARK_FK" FOREIGN KEY ("ID_MARK")
	  REFERENCES "EV_REPORT_MARK" ("ID") ENABLE;
  ALTER TABLE "EV_GROUP_VALUE" ADD CONSTRAINT "GROUP_REPORT_FK" FOREIGN KEY ("ID_REPORT")
	  REFERENCES "EV_REPORT" ("ID") ENABLE;