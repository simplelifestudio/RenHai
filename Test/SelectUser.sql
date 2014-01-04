set @deviceId = (select deviceId from device where deviceSn like "%MA-100%");

set @profileId = (select profileId from profile where deviceId = @deviceId);
set @impressCardId = (select impressCardId from impresscard where profileId = @profileId);
set @interestCardId = (select interestCardId from interestcard where profileId = @profileId);

select * from impresslabelmap where impressCardId = @impressCardId;
select * from impresscard where impressCardId = @impressCardId;

select * from interestlabelmap where interestCardId = @interestCardId;
select * from interestcard where interestCardId = @interestCardId;

select * from profile where profileId = @profileId;
select * from devicecard where deviceId = @deviceId;

select * from device where deviceId = @deviceId;

