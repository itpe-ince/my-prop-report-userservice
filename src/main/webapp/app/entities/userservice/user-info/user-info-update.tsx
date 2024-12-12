import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { GenderType } from 'app/shared/model/enumerations/gender-type.model';
import { createEntity, getEntity, reset, updateEntity } from './user-info.reducer';

export const UserInfoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userInfoEntity = useAppSelector(state => state.userservice.userInfo.entity);
  const loading = useAppSelector(state => state.userservice.userInfo.loading);
  const updating = useAppSelector(state => state.userservice.userInfo.updating);
  const updateSuccess = useAppSelector(state => state.userservice.userInfo.updateSuccess);
  const genderTypeValues = Object.keys(GenderType);

  const handleClose = () => {
    navigate(`/userservice/user-info${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...userInfoEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          gender: 'MALE',
          ...userInfoEntity,
          createdAt: convertDateTimeFromServer(userInfoEntity.createdAt),
          updatedAt: convertDateTimeFromServer(userInfoEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="userserviceApp.userserviceUserInfo.home.createOrEditLabel" data-cy="UserInfoCreateUpdateHeading">
            <Translate contentKey="userserviceApp.userserviceUserInfo.home.createOrEditLabel">Create or edit a UserInfo</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="user-info-id"
                  label={translate('userserviceApp.userserviceUserInfo.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.userId')}
                id="user-info-userId"
                name="userId"
                data-cy="userId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.firstname')}
                id="user-info-firstname"
                name="firstname"
                data-cy="firstname"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.lastname')}
                id="user-info-lastname"
                name="lastname"
                data-cy="lastname"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.alias')}
                id="user-info-alias"
                name="alias"
                data-cy="alias"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.gender')}
                id="user-info-gender"
                name="gender"
                data-cy="gender"
                type="select"
              >
                {genderTypeValues.map(genderType => (
                  <option value={genderType} key={genderType}>
                    {translate(`userserviceApp.GenderType.${genderType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.email')}
                id="user-info-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.phone')}
                id="user-info-phone"
                name="phone"
                data-cy="phone"
                type="text"
                validate={{
                  maxLength: { value: 15, message: translate('entity.validation.maxlength', { max: 15 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.addressLine1')}
                id="user-info-addressLine1"
                name="addressLine1"
                data-cy="addressLine1"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.addressLine2')}
                id="user-info-addressLine2"
                name="addressLine2"
                data-cy="addressLine2"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.city')}
                id="user-info-city"
                name="city"
                data-cy="city"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.country')}
                id="user-info-country"
                name="country"
                data-cy="country"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.createdAt')}
                id="user-info-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('userserviceApp.userserviceUserInfo.updatedAt')}
                id="user-info-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/userservice/user-info" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default UserInfoUpdate;
