// @ts-check
import { test, expect, chromium } from '@playwright/test';
import * as fs from 'fs';

test('In menu page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
});

test('Pending teachers page', async ({ page }) => {
    test.setTimeout(60000)
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('link', { name: 'Pending Teachers' }).click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.getByRole('heading', { name: 'Teachers Apply Requests' })).toBeVisible();

});

test('Course page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await expect(page.locator('h5').first()).toHaveText('ClassCode');
});

test('Create Course', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('link', { name: 'Create Course' }).click();
    await expect(page.getByRole('heading', { name: 'Select an GitHub Organization' })).toBeVisible();
});



test('Classroom page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await expect(page.getByText('i-on-ClassCode-Student - 1')).toBeVisible();
    await expect(page.getByRole('link', { name: 'Assignment title - Assignment description' })).toBeVisible();
});

test('Create Classroom', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('button', { name: 'Create Classroom' }).click();
    await page.getByLabel('Classroom Name').fill('New Classroom');
    await page.getByRole('button', { name: 'Create Classroom' }).click();
    await expect(page.getByRole('heading', { name: 'New Classroom' })).toBeVisible();
});

test('Archive Classroom', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.getByRole('heading', { name: 'New Classroom' }).click();
    await page.getByRole('button', { name: 'Archive Classroom' }).click();
    await expect(page.locator('h2').first()).toHaveText('ClassCode');
});

test('Assignment page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await expect(page.getByRole('heading', { name: 'Assignment title' })).toBeVisible();
    await expect(page.getByText('Assignment description')).toBeVisible();
});

test('Create Assignment', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.waitForLoadState('load');
    await page.getByRole('button', { name: 'Create Assignment' }).click();
    await page.getByLabel('Title').fill('New Assignment');
    await page.getByLabel('Description').fill('New Assignment Description');
    await page.getByRole('button', { name: 'Create' }).click();
    await expect(page.getByRole('heading', { name: 'New Assignment' ,exact:true})).toBeVisible();
});

test('Archive Assignment', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.waitForLoadState('load');
    await page.getByRole('link', { name: 'New Assignment - New Assignment Description' }).click();
    await page.waitForLoadState('load');
    await page.getByRole('button', { name: 'Delete Assignment' }).click();
    await expect(page.locator('h2').first()).toHaveText('ClassCode');
});

test('Team page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.getByRole('link', { name: 'More Info' }).click();
    await expect(page.getByRole('heading', { name: 'Team 1' })).toBeVisible();
});

test('Post feedback', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.getByRole('heading', { name: 'ClassCode' });
    await page.getByText('i-on-ClassCode-Student - 1');
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.getByRole('link', { name: 'More Info' }).click();
    await page.getByRole('button', { name: '​', exact: true }).click();
    await page.getByRole('option', { name: 'Info' }).click();
    await page.getByLabel('Description').fill('Demo');
    await page.getByRole('button', { name: 'Send Feedback' }).click();
    await page.goto('http://localhost:3000/courses/10/classrooms/10/assignments/11/teams/11');
    await expect(page.getByRole('heading', { name: 'info - Demo' }).first()).toBeVisible();
});

test('Delivery page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.waitForLoadState('load');
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('heading', { name: 'Delivery #0' }).click();
    await page.waitForLoadState('load');
    await expect(page.getByRole('heading', { name: 'Delivery' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'tag' })).toBeVisible();
});

test('Create Delivery', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.locator('h5').click();
    await page.locator('h5').first().click();
    await page.waitForLoadState('load');
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('link', { name: 'Create Delivery' }).click();
    await page.waitForLoadState('load');
    await page.getByLabel('Tag Control').fill('Demo Tag');
    await page.getByLabel('Due Date').fill('2050-01-01');
    await page.getByRole('button', { name: 'Create' }).click();
    await page.waitForLoadState('load');
    await expect(page.getByRole('heading', { name: 'Delivery' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Demo Tag' })).toBeVisible();
});
