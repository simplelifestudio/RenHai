set @deviceId = (select deviceId from device where deviceSn like "%D462AB5F378A");

set @profileId = (select profileId from profile where deviceId = @deviceId);
set @impressCardId = (select impressCardId from impresscard where profileId = @profileId);
set @interestCardId = (select interestCardId from interestcard where profileId = @profileId);

delete from impresslabelmap where impressCardId = @impressCardId;
delete from impresscard where impressCardId = @impressCardId;

delete from interestlabelmap where interestCardId = @interestCardId;
delete from interestcard where interestCardId = @interestCardId;

delete from profileoperationlog where profileId = @profileId;
delete from profile where profileId = @profileId;
delete from devicecard where deviceId = @deviceId;

delete from device where deviceId = @deviceId;

