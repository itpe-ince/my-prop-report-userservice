import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-info.reducer';

export const UserInfoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userInfoEntity = useAppSelector(state => state.userservice.userInfo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userInfoDetailsHeading">
          <Translate contentKey="userserviceApp.userserviceUserInfo.detail.title">UserInfo</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="userserviceApp.userserviceUserInfo.id">Id</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="userserviceApp.userserviceUserInfo.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.userId}</dd>
          <dt>
            <span id="firstname">
              <Translate contentKey="userserviceApp.userserviceUserInfo.firstname">Firstname</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.firstname}</dd>
          <dt>
            <span id="lastname">
              <Translate contentKey="userserviceApp.userserviceUserInfo.lastname">Lastname</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.lastname}</dd>
          <dt>
            <span id="alias">
              <Translate contentKey="userserviceApp.userserviceUserInfo.alias">Alias</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.alias}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="userserviceApp.userserviceUserInfo.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.gender}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="userserviceApp.userserviceUserInfo.email">Email</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="userserviceApp.userserviceUserInfo.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="userserviceApp.userserviceUserInfo.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="userserviceApp.userserviceUserInfo.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="userserviceApp.userserviceUserInfo.city">City</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.city}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="userserviceApp.userserviceUserInfo.country">Country</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.country}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="userserviceApp.userserviceUserInfo.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.createdAt ? <TextFormat value={userInfoEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="userserviceApp.userserviceUserInfo.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{userInfoEntity.updatedAt ? <TextFormat value={userInfoEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/userservice/user-info" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/userservice/user-info/${userInfoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserInfoDetail;
